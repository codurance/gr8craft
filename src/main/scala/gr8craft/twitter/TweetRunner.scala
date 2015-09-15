package gr8craft.twitter

import akka.persistence.PersistentActor
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.messages.{AddInspiration, Trigger}

class TweetRunner(twitterService: TwitterService, shelf: Shelf) extends PersistentActor {

  import context._

  override def persistenceId: String = "TweetRunner"

  override def receiveCommand: Receive = withShelf(shelf)

  override def receiveRecover: Receive = {
    case Trigger =>
    case AddInspiration(inspiration) =>
  }

  def withShelf(shelf: Shelf): Receive = {
    case Trigger => run(shelf)
    case AddInspiration(inspiration) => withInspiration(inspiration)
  }

  def withInspiration(inspiration: Inspiration): Unit = {
    become(withShelf(shelf.withInspiration(inspiration)))
  }

  def run(shelf: Shelf): Unit = {
    val inspiration = shelf.next
    twitterService.tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
  }

}
