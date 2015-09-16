package gr8craft.messages

import gr8craft.inspiration.Inspiration

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case object Next extends Message

case object Done extends Message

case object Skip extends Message

case class AddInspiration(inspiration: Inspiration) extends Message

case class Inspire(inspiration: Inspiration) extends Message

case class Tweet(tweet: String) extends Message

