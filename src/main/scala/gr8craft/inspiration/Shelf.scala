package gr8craft.inspiration

import akka.actor.Actor
import gr8craft.messages._


case class Shelf(inspirations: Set[Inspiration]) extends Actor {

  import context._

  override def receive: Receive = {
    receive(inspirations, 0)
  }

  def receive(inspirations: Set[Inspiration], index: Integer): Receive = {
    case InspireMe => inspireMe(inspirations, index)
    case Skip => changeTo(inspirations, index + 1)
    case AddInspiration(inspiration: Inspiration) => changeTo(inspirations + inspiration, index)
  }

  private def inspireMe(inspirations: Set[Inspiration], index: Integer): Unit = {
    if (inspirations.size <= index)
      return

    sender() ! Inspire(inspirations.toList(index))
    changeTo(inspirations, index + 1)
  }

  private def changeTo(inspirations: Set[Inspiration], index: Integer): Unit = {
    become(receive(inspirations, index))
  }

}
