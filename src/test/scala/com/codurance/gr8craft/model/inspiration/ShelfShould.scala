package com.codurance.gr8craft.model.inspiration

import akka.actor.Props
import akka.testkit.TestActorRef
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}
import com.codurance.gr8craft.util.AkkaTest

class ShelfShould extends AkkaTest("ShelfShould") {
  private val shelf = TestActorRef(Props(new Shelf(Set.empty)))
  private val inspiration = new Inspiration("topic", "location")

  test("return inspiration on the shelf") {
    shelf ! AddInspiration(inspiration)

    shelf ! InspireMe

    expectMsg(Inspire(inspiration))
  }

  test("add new inspiration to the end of the shelf") {
    val laterInspiration = new Inspiration("another topic", "another location")

    shelf ! AddInspiration(inspiration)
    shelf ! AddInspiration(laterInspiration)

    shelf ! InspireMe
    expectMsg(Inspire(inspiration))
    shelf ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("skip inspiration if instructed") {
    val laterInspiration = new Inspiration("another topic", "another location")

    shelf ! AddInspiration(inspiration)
    shelf ! AddInspiration(laterInspiration)

    shelf ! Skip

    shelf ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("give no inspiration if it runs out of inspiration") {
    shelf ! InspireMe

    expectNoMsg()
  }

  test("not allow the same inspiration twice in the shelf at one time") {
    val laterInspiration = new Inspiration("topic", "location")

    shelf ! AddInspiration(inspiration)
    shelf ! AddInspiration(laterInspiration)

    shelf ! InspireMe
    expectMsg(Inspire(inspiration))
    shelf ! InspireMe
    expectNoMsg()
  }
}
