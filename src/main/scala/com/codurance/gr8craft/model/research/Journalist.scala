package com.codurance.gr8craft.model.research

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._

class Journalist(researcher: ActorRef, archivist: ActorRef) extends PersistentActor {
  private var lastFetched: Option[DirectMessageId] = None

  override def persistenceId: String = "Journalist"

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerFetchingDirectMessages()
    case AddDirectMessage(directMessage) =>
      gotDirectMessage(directMessage)
  }

  override def receiveRecover: Receive = {
    case TriggeredResearcher(id) =>
      this.lastFetched = id
    case GotDirectMessage(directMessage) =>
      addInspirationFromDirectMessage(directMessage)
  }

  private def triggerFetchingDirectMessages(): Unit = {
    persist(TriggeredResearcher(lastFetched))(_ => {
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
      .foreach(inspiration => archivist ! AddInspiration(inspiration))
  }
}
