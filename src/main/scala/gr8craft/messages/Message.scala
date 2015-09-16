package gr8craft.messages

import gr8craft.inspiration.Inspiration

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case class AddInspiration(inspiration: Inspiration) extends Message

case class Tweet(tweet: String) extends Message
