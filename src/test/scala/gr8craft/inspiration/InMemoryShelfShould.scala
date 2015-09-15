package gr8craft.inspiration

import org.scalatest.{FunSuite, Matchers}

class InMemoryShelfShould extends FunSuite with Matchers {
  test("return inspirations on the shelf first in first out") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(Set(inspiration, laterInspiration))

    shelf.next shouldBe inspiration
    shelf.next shouldBe laterInspiration
  }

  test("add new inspirations to the end of the shelf") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(Set())
      .withInspiration(inspiration)
    shelf.next shouldBe inspiration

    val updatedShelf = shelf.withInspiration(laterInspiration)
    updatedShelf.next shouldBe laterInspiration
  }
  
  test("give default inspiration if it runs out of inspirations") {
    val shelf = new InMemoryShelf(Set())

    val inspiration = shelf.next

    inspiration.topic shouldBe "Interaction Driven Design"
    inspiration.location shouldBe "http://www.ustream.tv/recorded/61480606"
  }

  test("not allow the same inspiration in the shelf at one time") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("topic", "location")

    val shelf = new InMemoryShelf(Set())
      .withInspiration(inspiration)
      .withInspiration(laterInspiration)
    shelf.next

    val newInspiration = shelf.next

    newInspiration.topic shouldBe "Interaction Driven Design"
    newInspiration.location shouldBe "http://www.ustream.tv/recorded/61480606"
  }
}
