package com.codurance.gr8craft.model.scheduling

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.{Suggestion, Inspiration}
import com.codurance.gr8craft.model.twitter.DirectMessage

class RegularActions(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {
  private var lastFetched: Option[Long] = None

  override def persistenceId: String = "RegularActions"

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
      tweeter ! FetchDirectMessages(lastFetched)
    })
  }

  private def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      tweeter ! GoAndTweet(inspiration)
    )
  }
}