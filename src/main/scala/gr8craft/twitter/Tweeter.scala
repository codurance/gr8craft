package gr8craft.twitter

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import gr8craft.inspiration.{Submission, Inspiration}
import gr8craft.messages._

import scala.collection.Set
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
    val actorToInform = sender()
    val future = twitterService.getDirectMessagesFrom(lastFetched)

    future.onComplete {
      case Success(messages) => addInspirations(messages, actorToInform)
      case Failure(throwable) =>
    }
  }

  def addInspirations(messages: Set[DirectMessage], actorToInform: ActorRef): Unit = {
    messages.filter(message =>
      message.sender == "gr8craftmod")
      .map(message =>
      new Submission(message.directMessage).parse)
      .foreach(option =>
      option.foreach(inspiration =>
        actorToInform ! AddInspiration(inspiration)))
  }
}
