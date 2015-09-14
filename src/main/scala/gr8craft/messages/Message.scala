package gr8craft.messages

import gr8craft.article.Article

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case class AddInspiration(article: Article) extends Message
