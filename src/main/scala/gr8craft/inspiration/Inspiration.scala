package gr8craft.inspiration

class Inspiration(_topic: String, _location: String, _contributor: String = "") extends Serializable {
  def topic = _topic

  def location = _location

  override def equals(other: Any) = other match {
    case that: Inspiration => this.topic == that.topic && this.location == that.location
    case _ => false
  }

  override def hashCode = 41 * (41 + topic.hashCode) + location.hashCode

  override def toString: String = "Inspiration: topic: %s location: %s".format(topic, location)
}
