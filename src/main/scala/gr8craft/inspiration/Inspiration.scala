package gr8craft.inspiration

import scala.Option.empty

class Inspiration(_topic: String, _location: String, _contributor: Option[String] = empty) extends Serializable {
  val FORMATTING = s"Your hourly recommended inspiration about $topic: $location"

  def topic = _topic

  def location = _location

  override def equals(other: Any) = other match {
    case that: Inspiration => this.topic == that.topic && this.location == that.location
    case _ => false
  }

  override def hashCode = 41 * (41 + topic.hashCode) + location.hashCode

  override def toString: String = {
    FORMATTING + _contributor.fold(ifEmpty = "")(contributor => s" (via ${contributor})")
  }
}
