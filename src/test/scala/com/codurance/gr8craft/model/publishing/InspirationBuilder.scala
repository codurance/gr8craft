package com.codurance.gr8craft.model.publishing

import com.codurance.gr8craft.model.inspiration.Inspiration

class InspirationBuilder() {
  var topic = "topic"
  var location: String = "location"
  var contributor: Option[String] = None

  def withTopic(topic: String) = {
    this.topic = topic
    this
  }

  def withLocation(location: String) = {
    this.location = location
    this
  }

  def withContributor(contributor: String) = {
    this.contributor = Some(contributor)
    this
  }

  def build() = Inspiration(topic, location, contributor)
}
