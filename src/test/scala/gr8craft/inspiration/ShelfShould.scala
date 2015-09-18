package gr8craft.inspiration

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import gr8craft.messages._
import org.scalatest.{FunSuiteLike, Matchers, OneInstancePerTest}

class ShelfShould extends TestKit(ActorSystem("ShelfShould")) with FunSuiteLike with Matchers with ImplicitSender with OneInstancePerTest {
  val shelf = TestActorRef(Props(new Shelf(Set.empty)))
  val inspiration = new Inspiration("topic", "location")

  test("return inspirations on the shelf") {
    shelf ! AddInspiration(inspiration)

    shelf ! InspireMe

    expectMsg(Inspire(inspiration))
  }

  test("add new inspirations to the end of the shelf") {
    val laterInspiration = new Inspiration("another topic", "another location")

    shelf ! AddInspiration(inspiration)
    shelf ! AddInspiration(laterInspiration)

    shelf ! InspireMe
    expectMsg(Inspire(inspiration))
    shelf ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("skip inspirations if instructed") {
    val laterInspiration = new Inspiration("another topic", "another location")

    shelf ! AddInspiration(inspiration)
    shelf ! AddInspiration(laterInspiration)

    shelf ! Skip

    shelf ! InspireMe
    expectMsg(Inspire(laterInspiration))
  }

  test("give no inspiration if it runs out of inspirations") {
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
