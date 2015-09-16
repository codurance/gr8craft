package gr8craft.inspiration

import akka.actor.Actor
import gr8craft.messages._


case class Shelf(inspirations: Set[Inspiration]) extends Actor {

  import context._

  var index = 0

  override def receive: Receive = {
    withInspirations(inspirations)
  }

  def withInspirations(inspirations: Set[Inspiration]): Receive = {
    case Next => next(inspirations)
    case Skip => index = index + 1
    case AddInspiration(inspiration: Inspiration) => addInspiration(inspirations, inspiration)
  }

  def next(inspirations: Set[Inspiration]): Unit = {
    if (inspirations.size <= index) return
    val next = inspirations.toList(index)
    index = index + 1
    sender() ! Inspire(next)
  }

  def addInspiration(inspirations: Set[Inspiration], inspiration: Inspiration): Unit = {
    become(withInspirations(inspirations.drop(index) + inspiration))
  }
}
