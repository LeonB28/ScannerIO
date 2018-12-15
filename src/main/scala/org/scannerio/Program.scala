package org.scannerio

import org.scannerio.Entites.Target
import org.scannerio.Repositories._
import scalaz.zio.{App, IO}


object Program extends App {
  override def run(args: List[String]): IO[Nothing, Program.ExitStatus] = {
    val repo1 = new DefaultTargetRepository()
    val repo2 = new DefaultScanTaskExecutor()
    val repo3 = new DefaultNotification

    program(repo1, repo2, repo3)
      .map(_.foreach(println))
      .leftMap(e => println(e.getMessage))
      .run.forever
      .attempt.map(_.fold(_ => 1, _ => 0))
      .map(ExitStatus.ExitNow(_))
  }

  def program(scanTargetRepository: TargetRepository,
              scanTaskExecutor: ScanTaskExecutor,
              notification: Notification): IO[Exception, List[String]] = {

    def waitForNextTarget: IO[Exception, Target] = for {
      maybeTarget <- scanTargetRepository.nextTargetToScan
      target <- maybeTarget.map(IO.point(_)).getOrElse {
        scanTargetRepository.pause
        waitForNextTarget
      }
    } yield target


    for {
      target <- waitForNextTarget
      tasks <- scanTargetRepository.tasksFor(target)
      notes <- IO.traverse(tasks) { task =>
        for {
          result <- scanTaskExecutor.runTask(task)
          _ <- scanTaskExecutor.persistResult(result)
          subs <- notification.subscriberList(target, task)
          notes <- IO.traverse(subs)(notification.notifySub)
        } yield notes
      }
    } yield notes.flatten
  }
}
