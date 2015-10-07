package com.codurance.gr8craft.model.publishing

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
      publish(inspiration)
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

  private def publish(inspiration: Inspiration): Unit = {
    persist(Published(inspiration))(_ =>
      publisher ! Publish(inspiration)
    )
  }
}
