package com.codurance.gr8craft.model.inspiration

import akka.actor.Actor
import com.codurance.gr8craft.messages.{AddInspiration, Inspire, InspireMe, Skip}

class Archivist(inspirations: Set[Inspiration]) extends Actor {

  private var usedInspirations: Set[Inspiration] = Set.empty
  private var newInspirations: Set[Inspiration] = inspirations

  override def receive: Receive = {
    case InspireMe =>
      inspireMe()
    case Skip =>
      takeNextInspirationFromShelf()
    case AddInspiration(inspiration: Inspiration) =>
      newInspirations = newInspirations + inspiration
  }

  private def inspireMe(): Unit = {
    val nextInspiration = takeNextInspirationFromShelf()

    if (nextInspiration.isEmpty)
      return

    sender() ! Inspire(nextInspiration.get)
  }

  private def takeNextInspirationFromShelf(): Option[Inspiration] = {
    val nextInspiration = newInspirations.headOption

    if (nextInspiration.isDefined) {
      newInspirations = newInspirations.tail
      usedInspirations = usedInspirations + nextInspiration.get
    }

    nextInspiration
  }

}
