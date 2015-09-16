package gr8craft.twitter

import akka.actor.Actor
import gr8craft.messages.Tweet

class Tweeter(twitterService: TwitterService) extends Actor {
  override def receive: Receive = {
    case Tweet(tweet: String) => twitterService.tweet(tweet)
  }
}
