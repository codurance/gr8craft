package gr8craft.inspiration

import akka.actor.Props
import akka.testkit.TestActorRef
import gr8craft.AkkaTest
import gr8craft.messages._

class ShelfShould extends AkkaTest("ShelfShould") {
  val shelf = TestActorRef(Props(new Shelf(Set.empty)))
  val inspiration = new Inspiration("topic", "location")

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
