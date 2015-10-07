package com.codurance.gr8craft.model.inspiration

import org.scalatest.{FunSuite, Matchers}

class ShelfShould extends FunSuite with Matchers {

  private val inspiration = Inspiration("topic", "location")
  private val laterInspiration = Inspiration("laterTopic", "laterLocation")

  private val shelf = new Shelf()

  test("return nothing if the archivist is empty") {
    shelf.next() shouldBe None
  }

  test("return the first inspiration added") {
    val shelf = new Shelf(Set(inspiration))

    shelf.next() shouldBe Some(inspiration)
  }

  test("add new inspirations at the end of the archivist and return each only once") {
    shelf.addInspiration(inspiration)
    shelf.addInspiration(laterInspiration)

    shelf.next() shouldBe Some(inspiration)
    shelf.next() shouldBe Some(laterInspiration)
  }

  test("not allow the same inspiration on the archivist at the same time") {
    val sameInspiration = Inspiration("topic", "location")

    shelf.addInspiration(inspiration)
    shelf.addInspiration(sameInspiration)

    shelf.next() shouldBe Some(inspiration)
    shelf.next() shouldBe None
  }
}
