package gr8craft.twitter

import akka.actor.Actor
import gr8craft.inspiration.Shelf
import gr8craft.messages.{AddInspiration, Trigger}


class TweetRunner(twitterService: TwitterService, shelf: Shelf) extends Actor {
  def run(): Unit = {
    val inspiration = shelf.next
    twitterService.tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
  }

  override def receive = {
    case Trigger => run()
    case AddInspiration(inspiration) => shelf.withInspiration(inspiration)
  }
}
