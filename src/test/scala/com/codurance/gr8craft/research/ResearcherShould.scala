package com.codurance.gr8craft.research

import akka.actor.Props
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.publishing._
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ResearcherShould extends AkkaTest("ResearcherShould") with ScalaFutures {

  private val directMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: topic | location: location | contributor: contributor", DirectMessageId(1L))
  private val laterDirectMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: anotherTopic | location: anotherLocation | contributor: anotherContributor", DirectMessageId(2L))
  private val foreignMessage: DirectMessage = DirectMessage("someone else", "inspiration: topic | location: location | contributor: contributor", DirectMessageId(3L))
  private val lastRequested = DirectMessageId(42L)

  private var directMessages: List[DirectMessage] = List()

  private val twitterService = new TwitterService {
    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {}

    override def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = {
      successAction.apply(directMessages)
    }
  }

  private val researcher = system.actorOf(Props(new Researcher(twitterService)))

  test("don't accept direct messages that do not come from the moderator") {
    directMessages = List(foreignMessage)

    researcher ! FetchDirectMessages(Some(lastRequested))

    expectNoMsg()
  }

  test("fetch direct messages sent from the moderator") {
    directMessages = List(directMessage, laterDirectMessage)

    researcher ! FetchDirectMessages(Some(lastRequested))

    expectMsg(AddDirectMessage(directMessage))
    expectMsg(AddDirectMessage(laterDirectMessage))
  }
}
