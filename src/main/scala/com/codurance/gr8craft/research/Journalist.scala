package com.codurance.gr8craft.research

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Suggestion
import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId}

class Journalist(researcher: ActorRef, shelf: ActorRef) extends PersistentActor {
  private var lastFetched: Option[DirectMessageId] = None

  override def persistenceId: String = "Journalist"

  override def receiveRecover: Receive = {
    case Triggered(id) =>
      this.lastFetched = id
    case GotDirectMessage(directMessage) =>
      addInspirationFromDirectMessage(directMessage)
  }

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerFetchingDirectMessages()
    case AddDirectMessage(directMessage) =>
      gotDirectMessage(directMessage)
  }

  private def triggerFetchingDirectMessages(): Unit = {
    persist(Triggered(lastFetched))(_ => {
      researcher ! FetchDirectMessages(lastFetched)
    })
  }

  private def gotDirectMessage(directMessage: DirectMessage): Unit = {
    persist(GotDirectMessage(directMessage))(_ => {
      addInspirationFromDirectMessage(directMessage)
    })
  }

  private def addInspirationFromDirectMessage(directMessage: DirectMessage): Unit = {
    lastFetched = Some(directMessage.id)
    new Suggestion(directMessage.text).parse
      .foreach(inspiration => shelf ! AddInspiration(inspiration))
  }
}
