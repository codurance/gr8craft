package com.codurance.gr8craft.model.inspiration

import akka.actor.Actor
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}

class Archivist(shelf: Shelf) extends Actor {
  override def receive: Receive = {
    case InspireMe =>
      inspireMe()
    case Skip =>
      shelf.next()
    case AddInspiration(inspiration: Inspiration) =>
      shelf.addInspiration(inspiration)
  }

  private def inspireMe(): Unit = {
    val nextInspiration = shelf.next()

    nextInspiration.foreach(inspiration =>
      sender() ! Inspire(inspiration))
  }
}
