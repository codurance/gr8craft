package com.codurance.gr8craft.model.research

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId, TwitterService}

class Researcher(twitterService: TwitterService) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case FetchDirectMessages(lastFetched) =>
      fetchDirectMessages(lastFetched)
  }

  private def fetchDirectMessages(lastFetched: Option[DirectMessageId]): Unit = {
    val actorToInform = sender()
    twitterService.fetchDirectMessagesAfter(lastFetched, messages => addInspirations(messages, actorToInform))
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
