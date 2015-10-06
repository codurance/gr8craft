package com.codurance.gr8craft

import akka.actor.ActorSystem
import akka.testkit.JavaTestKit
import com.codurance.gr8craft.messages.{Start, Stop}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class Gr8craftShould extends FunSuite with MockFactory {
  val system = ActorSystem("Gr8craftShould")
  val scheduler = new JavaTestKit(system)
  val applicationRunner = new Gr8craft(scheduler.getRef)

  test("schedule the execution") {
    applicationRunner.startTwitterBot()

    scheduler.expectMsgEquals(1.second, Start)
  }

  test("stop the execution") {
    applicationRunner.stop()

    scheduler.expectMsgEquals(1.second, Stop)
  }
}
