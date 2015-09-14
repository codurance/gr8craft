package gr8craft.scheduling

import akka.actor.{ActorSystem, Props}
import akka.pattern.Patterns
import akka.pattern.Patterns.ask
import akka.testkit.{DefaultTimeout, JavaTestKit, TestActorRef, TestKit}
import gr8craft.messages.{Start, IsTerminated, Stop, Trigger}
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ScheduledExecutorShould extends TestKit(ActorSystem("ScheduledExecutorShould")) with FunSuiteLike with DefaultTimeout with Matchers with BeforeAndAfterAll with OneInstancePerTest with ScalaFutures {

  val toBeScheduled = new JavaTestKit(system)
  val scheduler = TestActorRef(Props(new ScheduledExecutor(1.nanosecond, toBeScheduled.getRef)))

  override def afterAll() {
    scheduler ! Stop
  }

  test("send trigger message to actor to be scheduled") {
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

  def ensureSchedulerIsTerminatedIs(isTerminated: Boolean): Unit = {
    whenReady(ask(scheduler, IsTerminated, 2000)) { answer =>
      answer shouldBe isTerminated
    }
  }

  def ensureTriggerMessageWasSent(): Unit = {
    toBeScheduled.expectMsgEquals(1.second, Trigger)
  }

}
