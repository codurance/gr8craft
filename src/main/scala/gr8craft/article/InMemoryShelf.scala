package gr8craft.article


class InMemoryShelf extends Shelf {
  var index = 0
  var articles: Seq[Article] = Seq()

  override def next: Article = {
    val next = articles(index)
    index = index + 1
    next
  }

  override def add(article: Article): Unit = articles = articles :+ article
}
