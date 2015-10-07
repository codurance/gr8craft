package com.codurance.gr8craft.model.publishing

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.research.DirectMessage

class Publisher(tweetSender: TweetSender) extends Actor {
  override def receive: Receive = {
    case Publish(inspiration) =>
      tweet(inspiration)
  }

  private def tweet(inspiration: Inspiration): Unit = {
    tweetSender.tweet(new Tweet(inspiration))
  }
}
