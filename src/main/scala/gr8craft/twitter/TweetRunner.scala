package gr8craft.twitter

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.messages.{Tweet, AddInspiration, Trigger}

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

class TweetRunner(tweeter: ActorRef, shelf: Shelf) extends PersistentActor {

  import context._

  override def persistenceId: String = "TweetRunner"

  override def receiveCommand: Receive = withShelf(shelf)

  override def receiveRecover: Receive = {
    case Trigger =>
    case AddInspiration(inspiration) =>
  }

  def withShelf(shelf: Shelf): Receive = {
    case Trigger => run(shelf)
    case AddInspiration(inspiration) => addInspiration(inspiration)
  }

  def addInspiration(inspiration: Inspiration): Unit = {
    persist(Added(inspiration))(_ =>
      withInspiration(inspiration))
  }

  def withInspiration(inspiration: Inspiration): Unit = {
    become(withShelf(shelf.withInspiration(inspiration)))
  }

  def run(shelf: Shelf): Unit = {
    val inspiration = shelf.next

    persist(Tweeted(inspiration))(_ =>
      tweeter ! Tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
    )
  }
}
