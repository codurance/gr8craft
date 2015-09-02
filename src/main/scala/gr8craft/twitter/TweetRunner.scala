package gr8craft.twitter

import gr8craft.article.Shelf

class TweetRunner(twitterService: TwitterService, shelf: Shelf) {

  def run() = { () =>
    val article = shelf.first
    twitterService.tweet(s"Your hourly recommended article about ${article.topic}: ${article.location}")
  }
}
