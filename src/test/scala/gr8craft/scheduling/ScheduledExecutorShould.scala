package gr8craft.scheduling

import org.junit.runner.RunWith
import org.scalatest.concurrent.Eventually
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers, OneInstancePerTest}

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ScheduledExecutorShould extends FunSuite with Matchers with Eventually with BeforeAndAfter with OneInstancePerTest {

  var wasScheduled = false
  val scheduler = new ScheduledExecutor(1.nanosecond, new Runnable {
    override def run(): Unit = wasScheduled = true
  })

  after(scheduler.shutdown())

  test("schedule the runnable") {
    scheduler.schedule()

    ensureRunnableWasScheduled
    scheduler.isShutDown shouldBe false
  }

  test("shutdown the runnable") {
    scheduler.schedule()
    ensureRunnableWasScheduled

    scheduler.shutdown()

    scheduler.isShutDown shouldBe true
  }

  def ensureRunnableWasScheduled: Unit = {
    eventually(timeout(5.seconds), interval(1.seconds)) {
      wasScheduled shouldBe true
    }
  }
}
