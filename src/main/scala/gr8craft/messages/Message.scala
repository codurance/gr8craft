package gr8craft.messages

import java.time.LocalDateTime
import java.time.LocalDateTime.MIN

import gr8craft.inspiration.Inspiration

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case object InspireMe extends Message

case object Done extends Message

case object Skip extends Message

case class SuccessfullyTweeted(inspiration: Inspiration) extends Message

case class FailedToTweet(inspiration: Inspiration) extends Message

case class AddInspiration(inspiration: Inspiration) extends Message

case class Inspire(inspiration: Inspiration) extends Message

case class Tweet(tweet: Inspiration) extends Message

case class DirectMessage(sender: String, directMessage: String) extends Message

case class FetchDirectMessages(lastFetched: LocalDateTime) extends Message

