package gr8craft.scheduling

import akka.actor.Props
import akka.pattern.Patterns.ask
import akka.testkit.{DefaultTimeout, JavaTestKit, TestActorRef}
import gr8craft.AkkaTest
import gr8craft.messages.{IsTerminated, Start, Stop, Trigger}
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ScheduledExecutorShould extends AkkaTest("ScheduledExecutorShould") with DefaultTimeout with BeforeAndAfterAll with ScalaFutures {

  val toBeScheduled = new JavaTestKit(system)
  val scheduler = TestActorRef(Props(new ScheduledExecutor(1.nanosecond, toBeScheduled.getRef)))

  override def afterAll() {
    scheduler ! Stop
  }

  test("send trigger text to actor to be scheduled") {
    scheduler ! Start

    ensureTriggerMessageWasSent()

    ensureSchedulerIsTerminatedIs(false)
  }

  test("stop the scheduler") {
    scheduler ! Start
    ensureTriggerMessageWasSent()

    scheduler ! Stop

    ensureSchedulerIsTerminatedIs(true)
  }

  private def ensureSchedulerIsTerminatedIs(isTerminated: Boolean): Unit = {
    whenReady(ask(scheduler, IsTerminated, 2000)) { answer =>
      answer shouldBe isTerminated
    }
  }

  private def ensureTriggerMessageWasSent(): Unit = {
    toBeScheduled.expectMsgEquals(1.second, Trigger)
  }

}
