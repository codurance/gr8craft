package gr8craft.inspiration

import scala.Option._

case class Inspiration(topic: String, location: String, optionalContributor: Option[String] = empty)
