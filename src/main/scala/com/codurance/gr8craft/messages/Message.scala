package com.codurance.gr8craft.messages

import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.twitter.DirectMessage

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case object InspireMe extends Message

case object Skip extends Message

case class SuccessfullyTweeted(inspiration: Inspiration) extends Message

case class FailedToTweet(inspiration: Inspiration) extends Message

case class AddInspiration(inspiration: Inspiration) extends Message

case class Inspire(inspiration: Inspiration) extends Message

case class GoAndTweet(tweet: Inspiration) extends Message

case class AddDirectMessage(directMessage: DirectMessage) extends Message

case class FetchDirectMessages(lastFetched: Option[Long]) extends Message
