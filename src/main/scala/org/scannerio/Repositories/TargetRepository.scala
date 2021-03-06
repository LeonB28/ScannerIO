package org.scannerio.Repositories

import org.scannerio.Entites.{ScanTask, Target, TaskAnalyze}
import scalaz.zio.IO

trait TargetRepository {
  def nextTargetToScan: IO[Exception, Option[Target]]
  def analyzeTaskFor(scanTask: ScanTask): IO[Exception, TaskAnalyze]
}

class DefaultTargetRepository extends TargetRepository {
  override def nextTargetToScan: IO[Exception, Option[Target]] = {
    val tasks = Seq(
      ScanTask("one"),
      ScanTask("two")
    )

    IO.succeedLazy(Some(Target(name = "target", tasks = tasks)))
  }

  override def analyzeTaskFor(scanTask: ScanTask): IO[Exception, TaskAnalyze] = ???
}

