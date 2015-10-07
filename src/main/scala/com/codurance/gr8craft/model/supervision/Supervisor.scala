package com.codurance.gr8craft.model.supervision

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.{Inspiration, Suggestion}
import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId}

class Supervisor(publisher: ActorRef, shelf: ActorRef) extends PersistentActor {
  private var lastFetched: Option[DirectMessageId] = None

  override def persistenceId: String = "Supervisor"

  override def receiveRecover: Receive = {
    case Triggered(id) =>
      shelf ! Skip
      this.lastFetched = id
    case Tweeted(inspiration) =>
    case GotDirectMessage(directMessage) =>
      addInspirationFromDirectMessage(directMessage)
  }

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerRegularActions()
    case Inspire(inspiration) =>
      tweet(inspiration)
    case AddDirectMessage(directMessage) =>
      gotDirectMessage(directMessage)
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

  private def triggerRegularActions(): Unit = {
    persist(Triggered(lastFetched))(_ => {
      shelf ! InspireMe
      publisher ! FetchDirectMessages(lastFetched)
    })
  }

  private def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      publisher ! GoAndTweet(inspiration)
    )
  }
}
