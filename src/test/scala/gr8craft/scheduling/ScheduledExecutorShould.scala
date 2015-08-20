package gr8craft.scheduling

import java.util.concurrent.TimeUnit.NANOSECONDS
import scala.concurrent.duration._
import org.scalatest.concurrent.Eventually
import org.scalatest.{FunSuite, Matchers}

class ScheduledExecutorShould extends FunSuite with Matchers with Eventually {

  var wasScheduled = false
  val scheduler = new ScheduledExecutor(NANOSECONDS, new Runnable {
    override def run(): Unit = wasScheduled = true
  })

  test("schedule the runnable") {
    scheduler.schedule()
    eventually(timeout(5.nanoseconds), interval(1.nanosecond)) {
      wasScheduled shouldBe true
    }
  }
}
