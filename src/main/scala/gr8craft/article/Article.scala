package gr8craft.article

class Article(_topic: String, _location: String) {
  def topic = _topic

  def location = _location

  override def toString: String = "topic: %s location: %s".format(topic, location)
}
