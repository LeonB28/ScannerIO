package org.scannerio.Repositories

import org.scannerio.Entites.{ScanTask, Target}
import scalaz.zio.IO

trait ScanTargetRepository {
  def nextTargetToScan: IO[Exception, Target]
  def tasksFor(target: Target): IO[Exception, Seq[ScanTask]]
}

class ScanTarget extends ScanTargetRepository {
  override def nextTargetToScan: IO[Exception, Target] = IO.point(Target("target"))
  override def tasksFor(target: Target): IO[Exception, Seq[ScanTask]] = {
    val tasks = Seq(
      ScanTask("one"),
      ScanTask("two")
    )
    IO.point(tasks)
  }
}