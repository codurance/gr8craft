package gr8craft.main

import gr8craft.article.{Article, InMemoryShelf, Shelf}
import gr8craft.scheduling.{ScheduledExecutor, Scheduler}
import gr8craft.twitter.{TwitterApiService, TwitterService}
import twitter4j.TwitterFactory

class ApplicationRunner(scheduler: Scheduler, twitterService: TwitterService, shelf: Shelf) {
  def startTwitterBot() {
    if (scheduler.isTriggered) {
      val article = shelf.first
      twitterService.tweet("Your hourly recommended article about " + article.topic + ": " + article.location)
    }
  }

  def stop() {
    println("stop bot")
  }

}

object ApplicationRunner {


  def main(args: Array[String]) {
    val articles: List[Article] = List(new Article("dummy topic", "dummy URL"))
    val application = new ApplicationRunner(new ScheduledExecutor, new TwitterApiService(TwitterFactory.getSingleton), new InMemoryShelf(articles))
    application.startTwitterBot()
  }
}
