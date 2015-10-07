package com.codurance.gr8craft.model.research

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._

class Researcher(directMessagefetcher: DirectMessageFetcher) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case FetchDirectMessages(lastFetched) =>
      fetchDirectMessages(lastFetched)
  }

  private def fetchDirectMessages(lastFetched: Option[DirectMessageId]): Unit = {
    val actorToInform = sender()
    directMessagefetcher.fetchAfter(lastFetched, messages => addInspirations(messages, actorToInform))
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
