package com.codurance.gr8craft.model.inspiration

import akka.actor.Props
import akka.testkit.TestActorRef
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}
import com.codurance.gr8craft.util.AkkaTest

class ArchivistShould extends AkkaTest("ArchivistShould") {
  private val archivist = TestActorRef(Props(new Archivist(Set.empty)))
  private val inspiration = new Inspiration("topic", "location")

  test("return inspiration on the shelf") {
    archivist ! AddInspiration(inspiration)

    archivist ! InspireMe

    expectMsg(Inspire(inspiration))
  }

  test("add new inspiration to the end of the shelf") {
    val laterInspiration = new Inspiration("another topic", "another location")

    archivist ! AddInspiration(inspiration)
    archivist ! AddInspiration(laterInspiration)

    archivist ! InspireMe
    expectMsg(Inspire(inspiration))
    archivist ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("skip inspiration if instructed") {
    val laterInspiration = new Inspiration("another topic", "another location")

    archivist ! AddInspiration(inspiration)
    archivist ! AddInspiration(laterInspiration)

    archivist ! Skip

    archivist ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("give no inspiration if it runs out of inspiration") {
    archivist ! InspireMe

    expectNoMsg()
  }

  test("not allow the same inspiration twice in the shelf at one time") {
    val laterInspiration = new Inspiration("topic", "location")

    archivist ! AddInspiration(inspiration)
    archivist ! AddInspiration(laterInspiration)

    archivist ! InspireMe
    expectMsg(Inspire(inspiration))
    archivist ! InspireMe
    expectNoMsg()
  }
}
