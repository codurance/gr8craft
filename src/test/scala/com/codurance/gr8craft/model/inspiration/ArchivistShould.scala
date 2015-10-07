package com.codurance.gr8craft.model.inspiration

import akka.actor.Props
import akka.testkit.TestActorRef
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}
import com.codurance.gr8craft.model.publishing.InspirationBuilder
import com.codurance.gr8craft.util.AkkaTest
import org.scalamock.scalatest.MockFactory

class ArchivistShould extends AkkaTest("ArchivistShould") with MockFactory {
  private val shelf = mock[Shelf]
  private val archivist = TestActorRef(Props(new Archivist(shelf)))
  private val inspiration = new InspirationBuilder().build()

  test("add new inspiration to the archivist") {
    (shelf.addInspiration _).expects(inspiration)

    archivist ! AddInspiration(inspiration)

    expectNoMsg()
  }

  test("get inspiration from the archivist") {
    (shelf.next _).expects().returns(Some(inspiration))

    archivist ! InspireMe

    expectMsg(Inspire(inspiration))
  }

  test("skip inspiration on archivist") {
    (shelf.next _).expects()

    archivist ! Skip

    expectNoMsg()
  }

  test("give no inspiration if archivist has none") {
    (shelf.next _).expects().returns(None)

    archivist ! InspireMe

    expectNoMsg()
  }
}
