import gr8craft.ApplicationRunner
import gr8craft.scheduling.Scheduler
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}


class ApplicationRunnerShould extends FunSuite with MockFactory {
  val scheduler = stub[Scheduler]
  val applicationRunner = new ApplicationRunner(scheduler)


  test("schedule the execution") {
    applicationRunner.startTwitterBot()
    (scheduler.schedule _).verify()
  }

  test("stop the execution") {
    applicationRunner.stop()
    (scheduler.shutdown _).verify()
  }
}
