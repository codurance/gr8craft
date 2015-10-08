package com.codurance.gr8craft.model.supervision

import akka.actor.Props
import akka.pattern.Patterns.ask
import akka.testkit.{DefaultTimeout, JavaTestKit, TestActorRef}
import com.codurance.gr8craft.messages.{IsTerminated, Start, Stop, Trigger}
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class SupervisorShould extends AkkaTest("SupervisorShould") with DefaultTimeout with BeforeAndAfterAll with ScalaFutures {

  private val aCollaborator = new JavaTestKit(system)
  private val anotherCollaborator = new JavaTestKit(system)
  private val scheduler = TestActorRef(Props(new Supervisor(1.millisecond, List(aCollaborator.getRef, anotherCollaborator.getRef))))

  override def afterAll() {
    scheduler ! Stop
  }

  test("send trigger to collaborators to be scheduled") {
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
    aCollaborator.expectMsgEquals(3.second, Trigger)
    anotherCollaborator.expectMsgEquals(3.second, Trigger)
  }

}
