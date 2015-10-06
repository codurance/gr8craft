package com.codurance.gr8craft.model.scheduling

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.twitter.DirectMessage

class RegularActions(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {
  var lastFetched: Option[Long] = None

  override def persistenceId: String = "RegularActions"

  override def receiveRecover: Receive = {
    case Triggered(id) =>
      shelf ! Skip
      this.lastFetched = id
    case Tweeted(inspiration) =>
    case Added(inspiration) =>
      shelf ! AddInspiration(inspiration)
    case ReadDirectMessage(id) =>
      lastFetched = Some(id)
  }

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerRegularActions()
    case AddInspiration(inspiration) =>
      addInspiration(inspiration)
    case Inspire(inspiration) =>
      tweet(inspiration)
    case GotDirectMessage(directMessage) =>
      gotDirectMessage(directMessage)
  }

  private def gotDirectMessage(directMessage: DirectMessage): Unit = {
    persist(ReadDirectMessage(directMessage.id))(_ => {
      lastFetched = Some(directMessage.id)
    })
  }

  private def triggerRegularActions(): Unit = {
    persist(Triggered(lastFetched))(_ => {
      shelf ! InspireMe
      tweeter ! FetchDirectMessages(lastFetched)
    })
  }

  private def addInspiration(inspiration: Inspiration): Unit = {
    persist(Added(inspiration))(_ =>
      shelf ! AddInspiration(inspiration))
  }

  private def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      tweeter ! GoAndTweet(inspiration)
    )
  }
}
