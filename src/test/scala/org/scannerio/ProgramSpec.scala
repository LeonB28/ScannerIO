package org.scannerio
import org.scalatest.FunSpec
import org.scalamock.scalatest.MockFactory
import org.scannerio.Repositories.{Notification, ScanTaskExecutor, TargetRepository}
import scalaz.zio.{IO, RTS}
import org.scalatest.Matchers._
import org.scannerio.Entites.{ScanTask, Target, TaskAnalyze}
class ProgramSpec extends FunSpec with MockFactory with RTS {

  describe("A Program") {

    val repo2 = mock[ScanTaskExecutor]
    val repo3 = mock[Notification]

    val error1 = new Exception("no tasks")
    val error2 = new Exception("fail to run task")

    it("should throw error if failed to next fetch tasks") {
      val repo1 = mock[TargetRepository]
      repo1.nextTargetToScan _ expects () returning IO.fail(error1)
      unsafeRun(Program.program(repo1, repo2, repo3).attempt) shouldBe Left(error1)
    }

    it("should wait when no target available") {
      val repo1 = new MockedTargetRepository
      unsafeRun(Program.program(repo1, repo2, repo3).attempt) shouldBe Left(error2)
    }

    class MockedTargetRepository extends TargetRepository {
      var i = 0
      override def nextTargetToScan: IO[Exception, Option[Target]] = {
        IO.point {
          i = i + 1
          if (i < 3) None else Some(Target("mmm", Seq.empty))
        }
      }


      override def pause: IO[Exception, Unit] = IO.point(Unit)

      override def analyzeTaskFor(scanTask: ScanTask): IO[Exception, TaskAnalyze] = ???
    }
  }

}
