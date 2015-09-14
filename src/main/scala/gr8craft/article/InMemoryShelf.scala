package gr8craft.article


case class InMemoryShelf(articles: Seq[Article]) extends Shelf {
  var index = 0

  override def next: Article = {
    val next = articles(index)
    index = index + 1
    next
  }

  override def add(article: Article): Unit = article +: articles
}
