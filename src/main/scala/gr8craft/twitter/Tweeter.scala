package gr8craft.twitter

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import gr8craft.inspiration.{Inspiration, Submission}
import gr8craft.messages._

import scala.collection.Set
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Tweeter(twitterService: TwitterService) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case GoAndTweet(inspiration: Inspiration) => tweet(inspiration)
    case FetchDirectMessages(lastFetched: LocalDateTime) => fetchDirectMessages(lastFetched)
  }

  def tweet(inspiration: Inspiration): Unit = {
    val actorToInform = sender()
    val future = twitterService.tweet(new Tweet(inspiration).toString)

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
      message.sender == APPROVED_MODERATOR)
      .map(message =>
        new Submission(message.directMessage).parse)
      .foreach(option =>
        option.foreach(inspiration =>
          actorToInform ! AddInspiration(inspiration)))
  }
}
