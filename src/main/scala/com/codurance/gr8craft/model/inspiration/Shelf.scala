package com.codurance.gr8craft.model.inspiration

class Shelf(initialInspirations: Set[Inspiration] = Set()) {
  private var usedInspirations: Set[Inspiration] = Set.empty
  private var newInspirations: Set[Inspiration] = initialInspirations

  def next(): Option[Inspiration] = {
    val nextInspiration = newInspirations.headOption

    if (nextInspiration.isDefined) {
      newInspirations = newInspirations.tail
      usedInspirations = usedInspirations + nextInspiration.get
    }

    nextInspiration
  }

  def addInspiration(inspiration: Inspiration): Unit = {
    newInspirations = newInspirations + inspiration
  }
}
