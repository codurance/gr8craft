package gr8craft.article


case class Shelf(articles: Seq[Article]) {
  def first = articles.head

  def +:(newArticle: Article) = Shelf(newArticle +: articles)
}
