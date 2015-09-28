package gr8craft.twitter

import java.time.LocalDateTime

import akka.actor.Actor
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{FetchDirectMessages, FailedToTweet, SuccessfullyTweeted, Tweet}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Tweeter(twitterService: TwitterService) extends Actor {


  override def receive: Receive = {
    case Tweet(inspiration: Inspiration) => tweet(inspiration)
    case FetchDirectMessages(lastFetched: LocalDateTime) => fetchDirectMessages(lastFetched)
  }

  def tweet(inspiration: Inspiration): Unit = {
    val actorToInform = sender()
    val future = twitterService.tweet(inspiration.toString)

    future.onComplete {
      case Success(message) => actorToInform ! SuccessfullyTweeted(inspiration)
      case Failure(throwable) => actorToInform ! FailedToTweet(inspiration)
    }
  }

  def fetchDirectMessages(lastFetched: LocalDateTime): Unit = {
    twitterService.getDirectMessagesFrom(lastFetched)
  }
}
