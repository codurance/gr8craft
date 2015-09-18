package gr8craft.twitter

import akka.actor.Actor
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{FailedToTweet, SuccessfullyTweeted, Tweet}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Tweeter(twitterService: TwitterService) extends Actor {
  override def receive: Receive = {
    case Tweet(inspiration: Inspiration) => {

      val future = twitterService.tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
      
      future.onComplete {
        case Success(message) => sender() ! SuccessfullyTweeted(inspiration)
        case Failure(throwable) => sender() ! FailedToTweet(inspiration)
      }
    }
  }
}
