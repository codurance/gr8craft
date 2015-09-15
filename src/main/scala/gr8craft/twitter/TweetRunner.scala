package gr8craft.twitter

import akka.actor.Actor
import gr8craft.inspiration.Shelf
import gr8craft.messages.{AddInspiration, Trigger}

class TweetRunner(twitterService: TwitterService, shelf: Shelf) extends Actor {

  import context._

  override def receive = {
    withShelf(shelf)
  }

  def withShelf(shelf: Shelf): Receive = {
    case Trigger => run(shelf)
    case AddInspiration(inspiration) => become(withShelf(shelf.withInspiration(inspiration)))
  }

  def run(shelf: Shelf): Unit = {
    val inspiration = shelf.next
    twitterService.tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
  }
}
