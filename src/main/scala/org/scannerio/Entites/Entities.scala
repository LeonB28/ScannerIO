package org.scannerio.Entites

import scalaz.zio.IO

case class Target(name: String, tasks: Seq[ScanTask])
case class ScanTask(name: String)
case class ScanTaskResult(content: List[String])
case class ScanSubscriber(name: String)

abstract class TaskAnalyze(name: String) {
  def analyze(scanTaskResult: ScanTaskResult): IO[Exception, Boolean]
}
