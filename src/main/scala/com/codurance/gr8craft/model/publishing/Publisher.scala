package com.codurance.gr8craft.model.publishing

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration

class Publisher(tweetSender: TweetSender) extends Actor {
  private val APPROVED_MODERATOR = "gr8craftmod"

  override def receive: Receive = {
    case GoAndTweet(inspiration) =>
      tweet(inspiration)
  }

  private def tweet(inspiration: Inspiration): Unit = {
    val actorToInform = sender()
    tweetSender.tweet(new Tweet(inspiration), () => actorToInform ! SuccessfullyTweeted(inspiration))
  }

  def addInspirations(messages: List[DirectMessage], actorToInform: ActorRef): Unit = {
    messages
      .filter(message => message.sender == APPROVED_MODERATOR)
      .foreach(message => actorToInform ! AddDirectMessage(message))
  }
}
