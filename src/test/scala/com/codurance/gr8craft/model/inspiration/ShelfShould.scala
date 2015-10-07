package com.codurance.gr8craft.model.inspiration

import org.scalatest.{FunSuite, Matchers}

class ShelfShould extends FunSuite with Matchers {

  private val inspiration = Inspiration("topic", "location")
  private val laterInspiration = Inspiration("laterTopic", "laterLocation")

  test("return nothing if the shelf is empty") {
    val shelf = new Shelf(Set())

    shelf.next() shouldBe None
  }

  test("return the first inspiration added") {
    val shelf = new Shelf(Set(inspiration))

    shelf.next() shouldBe Some(inspiration)
  }

  test("add new inspirations at the end of the shelf and return each only once") {
    val shelf = new Shelf(Set())

    shelf.addInspiration(inspiration)
    shelf.addInspiration(laterInspiration)

    shelf.next() shouldBe Some(inspiration)
    shelf.next() shouldBe Some(laterInspiration)
  }
}
