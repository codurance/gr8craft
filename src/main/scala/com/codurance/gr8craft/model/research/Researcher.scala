package com.codurance.gr8craft.model.research

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._

class Researcher(directMessageFetcher: DirectMessageFetcher) extends Actor {
  private val ApprovedModerator = "gr8craftmod"

  override def receive: Receive = {
    case FetchDirectMessages(lastFetched) =>
      fetchDirectMessages(lastFetched)
  }

  private def fetchDirectMessages(lastFetched: Option[DirectMessageId]): Unit = {
    val actorToInform = sender()
    directMessageFetcher.fetchAfter(lastFetched, messages => addInspirations(messages, actorToInform))
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == ApprovedModerator)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
