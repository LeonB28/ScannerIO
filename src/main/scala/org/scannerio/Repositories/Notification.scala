package org.scannerio.Repositories
import org.scannerio.Entites.{ScanSubscriber, ScanTask, Target}
import scalaz.zio.IO
import scala.util.Random

trait Notification {
  def subscriberList(target: Target, task: ScanTask): IO[Exception, List[ScanSubscriber]]
  def notify(scanSubscriber: ScanSubscriber): IO[Exception, String]
}

class DefaultNotification extends Notification {
  override def subscriberList(target: Target, task: ScanTask): IO[Exception, List[ScanSubscriber]] = {
    val pre = s"${target.name}-${task.name}"
      val res = List(
      ScanSubscriber(s"$pre => Saba"),
      ScanSubscriber(s"$pre => Muki"),
      ScanSubscriber(s"$pre => Sabaz")
    )
    IO.point(res)
  }

  override def notify(subscriber: ScanSubscriber): IO[Exception, String] = {
    val head = Random.nextInt(2) == 1
    if (head && subscriber.name == "target-two => Muki") {
      IO.fail(s"Hate ${subscriber.name}").leftMap(new Exception(_))
    } else {
      IO.point(subscriber.name)
    }
  }
}