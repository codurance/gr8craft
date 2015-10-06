package gr8craft.twitter

import akka.actor.{Actor, ActorRef}
import gr8craft.inspiration.Inspiration
import gr8craft.messages._

import scala.collection.Set
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Tweeter(twitterService: TwitterService) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case GoAndTweet(inspiration) =>
      tweet(inspiration)
    case FetchDirectMessages(lastFetched) =>
      fetchDirectMessages(lastFetched)
  }

  def tweet(inspiration: Inspiration): Unit = {
    val actorToInform = sender()
    val future = twitterService.tweet(new Tweet(inspiration).toString)

    future.onComplete {
      case Success(message) =>
        actorToInform ! SuccessfullyTweeted(inspiration)
      case Failure(_) =>
        actorToInform ! FailedToTweet(inspiration)
    }
  }

  def fetchDirectMessages(lastFetched: Option[Long]): Unit = {
    val actorToInform = sender()
    val future = twitterService.getDirectMessagesAfter(lastFetched)

    future.onComplete {
      case Success(messages) =>
        addInspirations(messages, actorToInform)
      case _ =>
    }
  }

  def addInspirations(messages: Set[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! GotDirectMessage(message))
  }
}
