package com.codurance.gr8craft.model.publishing

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration

class Publisher(twitterService: TwitterService) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case GoAndTweet(inspiration) =>
      tweet(inspiration)
    case FetchDirectMessages(lastFetched) =>
      fetchDirectMessages(lastFetched)
  }

  private def tweet(inspiration: Inspiration): Unit = {
    val actorToInform = sender()
    twitterService.tweet(new Tweet(inspiration), () => actorToInform ! SuccessfullyTweeted(inspiration), () => actorToInform ! FailedToTweet(inspiration))
  }

  private def fetchDirectMessages(lastFetched: Option[DirectMessageId]): Unit = {
    val actorToInform = sender()
    twitterService.fetchDirectMessagesAfter(lastFetched, messages => addInspirations(messages, actorToInform))
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
