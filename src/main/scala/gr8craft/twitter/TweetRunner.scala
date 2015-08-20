package gr8craft.twitter

import gr8craft.article.Shelf

class TweetRunner(twitterService: TwitterService, shelf: Shelf) extends Runnable {
  override def run(): Unit = {
    val article = shelf.first
    twitterService.tweet("Your hourly recommended article about " + article.topic + ": " + article.location)
  }
}
