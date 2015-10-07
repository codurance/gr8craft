package com.codurance.gr8craft.model.supervision

import akka.actor.{ActorRef, Kill, Props}
import akka.testkit.TestProbe
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EditorShould extends AkkaTest("EditorShould") with MockFactory {
  private val inspiration = Inspiration("topic", "location")

  private val archivist = TestProbe()
  private val publisher = TestProbe()

  private var editor = createEditor()

  test("receive a trigger and ask the archivist for the next inspiration") {
    editor ! Trigger

    archivist.expectMsg(InspireMe)
    publisher.expectNoMsg()
  }

  test("receive a new inspiration and use it") {
    editor ! Inspire(inspiration)

    publisher.expectMsg(GoAndTweet(inspiration))
    archivist.expectNoMsg()
  }

  test("recover Trigger by skipping text to archivist") {
    editor ! Trigger
    archivist.expectMsg(InspireMe)

    recoverFromShutdown()

    archivist.expectMsg(Skip)
    publisher.expectNoMsg()
  }

  test("recover Inspire by doing nothing") {
    editor ! Inspire(inspiration)
    publisher.expectMsg(GoAndTweet(inspiration))

    recoverFromShutdown()

    archivist.expectNoMsg()
    publisher.expectNoMsg()
  }

  private def recoverFromShutdown(): Unit = {
    editor ! Kill
    editor = createEditor()
  }

  private def createEditor(): ActorRef = {
    system.actorOf(Props(new Editor(publisher.ref, archivist.ref)))
  }
}
