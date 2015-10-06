package com.codurance.gr8craft.model.inspiration

import scala.Option._

case class Inspiration(topic: String, location: String, optionalContributor: Option[String] = empty)
