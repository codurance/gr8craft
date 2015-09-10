package gr8craft.twitter

import akka.actor.Actor
import gr8craft.article.Shelf
import gr8craft.messages.Trigger


class TweetRunner(twitterService: TwitterService, shelf: Shelf) extends Actor {
  def run(): Unit = {
    val article = shelf.first
    twitterService.tweet(s"Your hourly recommended article about ${article.topic}: ${article.location}")
  }

  override def receive = {
    case Trigger => run()
  }
}
