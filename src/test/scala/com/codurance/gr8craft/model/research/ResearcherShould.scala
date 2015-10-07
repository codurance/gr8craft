package com.codurance.gr8craft.model.research

import akka.actor.Props
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ResearcherShould extends AkkaTest("ResearcherShould") with ScalaFutures {
  private val directMessage = new DirectMessageBuilder().withSender("gr8craftmod").withText("text").build()
  private val laterDirectMessage = new DirectMessageBuilder().withSender("gr8craftmod").withText("laterText").build()
  private val foreignMessage: DirectMessage = new DirectMessageBuilder().withSender("someone else").build()

  private val lastRequested = Some(DirectMessageId(42))

  private val directMessageFetcher = new DirectMessageFetcher {
    var messages: List[DirectMessage] = List()

    override def fetchAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = {
      successAction.apply(messages)
    }
  }

  private val researcher = system.actorOf(Props(new Researcher(directMessageFetcher)))

  test("don't accept direct messages that do not come from the moderator") {
    directMessageFetcher.messages = List(foreignMessage)

    researcher ! FetchDirectMessages(lastRequested)

    expectNoMsg()
  }

  test("fetch direct messages sent from the moderator") {
    directMessageFetcher.messages = List(directMessage, laterDirectMessage)

    researcher ! FetchDirectMessages(lastRequested)

    expectMsg(AddDirectMessage(directMessage))
    expectMsg(AddDirectMessage(laterDirectMessage))
  }
}
