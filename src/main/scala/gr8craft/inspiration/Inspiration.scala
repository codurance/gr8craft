package gr8craft.inspiration

import scala.Option.empty

class Inspiration(private val topic: String, private val location: String, private val optionalContributor: Option[String] = empty) extends Serializable {
  val FORMATTING = s"Your hourly recommended inspiration about $topic: $location"

  override def equals(other: Any) = other match {
    case that: Inspiration => topic == that.topic && location == that.location
    case _ => false
  }

  override def hashCode = 41 * (41 + topic.hashCode) + location.hashCode

  override def toString: String = {
    FORMATTING + optionalContributor.fold(ifEmpty = "")(contributor => s" (via $contributor)")
  }
}
