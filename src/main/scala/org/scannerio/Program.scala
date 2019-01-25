package org.scannerio

import org.scannerio.Repositories._
import scalaz.zio.{App, IO, Schedule}

object Program extends App {
  override def run(args: List[String]): IO[Nothing, Program.ExitStatus] = {
    val targets = new DefaultTargetRepository
    val taskExecutor = new DefaultScanTaskExecutor
    val notifier = new DefaultNotification

    program(targets, taskExecutor, notifier)
      .map(_.foreach(println))
      .leftMap(e => println(e.getMessage))
      .run.forever
      .attempt.map(_.fold(_ => 1, _ => 0))
      .map(ExitStatus.ExitNow(_))
  }

  def program(scanTargetRepository: TargetRepository,
              scanTaskExecutor: ScanTaskExecutor,
              notification: Notification): IO[Exception, List[String]] = {
    for {
      target <- scanTargetRepository.nextTargetToScan
        .repeat(Schedule.doWhile(_.isEmpty))
        .map(_.get)

      notes <- IO.traverse(target.tasks) { task =>
        for {
          result <- scanTaskExecutor.runTask(task)
          _ <- scanTaskExecutor.persistResult(result)
          analyzeTask <- scanTargetRepository.analyzeTaskFor(task)
          hasPattern <- analyzeTask.analyze(result)
          notificationAttempts <-
            if (hasPattern)
              for {
                subs <- notification.subscriberList(target, task)
                notes <- IO.traverse(subs)(notification.notifySub)
              } yield notes
            else IO.point(Seq.empty)
        } yield notificationAttempts
      }
    } yield notes.flatten
  }
}
