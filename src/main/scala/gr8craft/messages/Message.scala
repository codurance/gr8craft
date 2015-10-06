package gr8craft.messages

import gr8craft.inspiration.Inspiration
import gr8craft.twitter.DirectMessage

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

case class GoAndTweet(tweet: Inspiration) extends Message

case class GotDirectMessage(directMessage: DirectMessage) extends Message

case class FetchDirectMessages(lastFetched: Option[Long]) extends Message

