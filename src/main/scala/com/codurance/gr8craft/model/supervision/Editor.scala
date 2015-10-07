package com.codurance.gr8craft.model.supervision

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration

class Editor(publisher: ActorRef, archivist: ActorRef) extends PersistentActor {
  override def persistenceId: String = "Editor"

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerArchivist()
    case Inspire(inspiration) =>
      tweet(inspiration)
  }

  override def receiveRecover: Receive = {
    case TriggeredArchivist =>
      archivist ! Skip
  }


  private def triggerArchivist(): Unit = {
    persist(TriggeredArchivist)(_ => {
      archivist ! InspireMe
    })
  }

  private def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      publisher ! GoAndTweet(inspiration)
    )
  }
}
