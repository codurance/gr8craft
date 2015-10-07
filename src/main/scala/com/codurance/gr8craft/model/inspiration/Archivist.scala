package com.codurance.gr8craft.model.inspiration

import akka.actor.Actor
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}

class Archivist(inspirations: Set[Inspiration]) extends Actor {
  private val shelf = new Shelf(inspirations)

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

    if (nextInspiration.isEmpty)
      return

    sender() ! Inspire(nextInspiration.get)
  }
}
