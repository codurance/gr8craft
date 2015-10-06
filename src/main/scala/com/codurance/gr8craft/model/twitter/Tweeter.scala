package com.codurance.gr8craft.model.twitter

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration

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
    twitterService.tweet(new Tweet(inspiration), { () => actorToInform ! SuccessfullyTweeted(inspiration) }, { () => actorToInform ! FailedToTweet(inspiration) })
  }

  def fetchDirectMessages(lastFetched: Option[DirectMessageId]): Unit = {
    val actorToInform = sender()
    twitterService.fetchDirectMessagesAfter(lastFetched, { (messages) => addInspirations(messages, actorToInform) })
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
