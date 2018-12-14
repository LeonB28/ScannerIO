package org.scannerio
import org.scalatest.FunSpec
import org.scalamock.scalatest.MockFactory
import org.scannerio.Repositories.{Notification, ScanTargetRepository, ScanTaskExecutor}
import scalaz.zio.{IO, RTS}
import org.scalatest.Matchers._
class ProgramSpec extends FunSpec with MockFactory with RTS {

  describe("A Program") {

    val repo1 = mock[ScanTargetRepository]
    val repo2 = mock[ScanTaskExecutor]
    val repo3 = mock[Notification]

    val error1 = new Exception("no tasks")

    it("should throw error if failed to next fetch tasks") {
      unsafeRun(Program.program(repo1, repo2, repo3).attempt) shouldBe Left(error1)
    }

    repo1.nextTargetToScan _ expects () returning IO.fail(error1)
  }

}
