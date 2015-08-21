package gr8craft.scheduling

import java.util.concurrent.TimeUnit.NANOSECONDS
import scala.concurrent.duration._
import org.scalatest.concurrent.Eventually
import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite, Matchers}

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

    scheduler.isShutDown shouldBe true
  }
}
