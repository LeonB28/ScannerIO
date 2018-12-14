package org.scannerio.Repositories

import org.scannerio.Entites.{ScanTask, ScanTaskResult}
import scalaz.zio.IO

trait ScanTaskExecutor {
  def runTask(task: ScanTask): IO[Exception, ScanTaskResult]
  def persistResult(scanTaskResult: ScanTaskResult): IO[Exception, Unit]
}

class DefaultScanTaskExecutor extends ScanTaskExecutor {
  override def runTask(task: ScanTask): IO[Exception, ScanTaskResult] = {
    val res = ScanTaskResult(
      List("try1", "try2", "try3")
    )
    IO.point(res)
  }

  override def persistResult(scanTaskResult: ScanTaskResult): IO[Exception, Unit] = {
    IO.point(Unit)
  }
}
