package org.scannerio

import java.util.concurrent.TimeUnit

import org.scannerio.Entites.Target
import org.scannerio.Repositories._
import scalaz.zio.duration._
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

      nextTarget <- scanTargetRepository.nextTargetToScan
        .repeat(Schedule.doUntil[Option[Target]](maybe => maybe.isDefined)
          .delayed(_ => Duration(2, TimeUnit.MINUTES))) // wait if no target defined
        .map(_.get)

      sentEmailOutcomes <- IO.foreach(nextTarget.tasks) { task =>
        for {
          result <- scanTaskExecutor.runTask(task)
          _ <- scanTaskExecutor.persistResult(result)
          analyzeTask <- scanTargetRepository.analyzeTaskFor(task)
          hasPattern <- analyzeTask.analyze(result)
          notificationAttempts <-
            if (hasPattern)
              for {
                subs <- notification.subscriberList(nextTarget, task)
                notes <- IO.foreach(subs)(notification.notifySub)
              } yield notes
            else IO.succeedLazy(Seq.empty)
        } yield notificationAttempts
      }
    } yield sentEmailOutcomes.flatten
  }
}