package gr8craft.scheduling

import java.util.concurrent.TimeUnit.NANOSECONDS

import org.junit.runner.RunWith
import org.scalatest.concurrent.Eventually
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers, OneInstancePerTest}

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ScheduledExecutorShould extends FunSuite with Matchers with Eventually with BeforeAndAfter with OneInstancePerTest {

  var wasScheduled = false
  val scheduler = new ScheduledExecutor(NANOSECONDS, new Runnable {
    override def run(): Unit = wasScheduled = true
  })

  after(scheduler.shutdown())

  test("schedule the runnable") {
    scheduler.schedule()

    eventually(timeout(5.nanoseconds), interval(1.nanosecond)) {
      wasScheduled shouldBe true
    }
    scheduler.isShutDown shouldBe false
  }

  test("shutdown the runnable") {
    scheduler.schedule()
    eventually(timeout(5.nanoseconds), interval(1.nanosecond)) {
      wasScheduled shouldBe true
    }

    scheduler.shutdown()

    eventually(timeout(5.nanoseconds), interval(1.nanosecond)) {
      scheduler.isShutDown shouldBe true
    }
  }
}
