package gr8craft.article

import org.scalatest.{Matchers, FunSuite}

class InMemoryShelfShould extends FunSuite with Matchers {
  test("return articles on the shelf first in first out") {
    val article = new Article("topic", "location")
    val laterArticle = new Article("another topic", "another location")

    val shelf = new InMemoryShelf(List(article, laterArticle))

    shelf.next shouldBe article
    shelf.next shouldBe laterArticle
  }

  test("add new articles add the end of the shelf") {
    val article = new Article("topic", "location")
    val laterArticle = new Article("another topic", "another location")
    val shelf = new InMemoryShelf(List())

    shelf.add(article)
    shelf.add(laterArticle)

    shelf.next shouldBe article
    shelf.next shouldBe laterArticle
  }
}
