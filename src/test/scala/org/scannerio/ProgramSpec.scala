package org.scannerio
import org.scalatest.FunSpec
import org.scalamock.scalatest.MockFactory
import org.scannerio.Repositories.{Notification, TargetRepository, ScanTaskExecutor}
import scalaz.zio.{IO, RTS}
import org.scalatest.Matchers._
import org.scannerio.Entites.Target
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
      val repo1 = mock[TargetRepository]
      repo1.nextTargetToScan _ expects () returning IO.point(None) once()
      repo1.nextTargetToScan _ expects () returning IO.point(Some(Target("bla"))) once()
      repo1.pause _ expects () returning IO.point(Unit) once()
      repo1.tasksFor _ expects * returning IO.fail(error2) anyNumberOfTimes()

      unsafeRun(Program.program(repo1, repo2, repo3).attempt) shouldBe Left(error2)
    }


  }

}
