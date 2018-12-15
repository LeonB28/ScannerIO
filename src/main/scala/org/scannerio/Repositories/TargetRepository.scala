package org.scannerio.Repositories

import org.scannerio.Entites.{ScanTask, Target}
import scalaz.zio.IO

import scala.concurrent.duration._

trait TargetRepository {
  def nextTargetToScan: IO[Exception, Option[Target]]
  def tasksFor(target: Target): IO[Exception, Seq[ScanTask]]
  def pause: IO[Exception, Unit]
}

class DefaultTargetRepository extends TargetRepository {
  override def nextTargetToScan: IO[Exception, Option[Target]] = IO.point(Some(Target("target")))
  override def tasksFor(target: Target): IO[Exception, Seq[ScanTask]] = {
    val tasks = Seq(
      ScanTask("one"),
      ScanTask("two")
    )
    IO.point(tasks)
  }
  override def pause: IO[Exception, Unit] = IO.sleep(2.minutes)
}