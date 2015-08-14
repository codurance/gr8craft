package gr8craft.main

import gr8craft.article.{InMemoryShelf, Shelf}
import gr8craft.scheduling.Scheduler
import gr8craft.twitter.{TwitterApiService, TwitterService}

class ApplicationRunner(scheduler: Scheduler, twitterService: TwitterService, shelf: Shelf) {
  def startTwitterBot() = {
    if (scheduler.isTriggered) {
      val article = shelf.first
      twitterService.tweet("Your hourly recommended article about " + article.topic + ": " + article.location)
    }
  }

  def stop = {
    println("stop bot")
  }

}

object ApplicationRunner {

  def createScheduler: Scheduler = new Scheduler {
    override def isTriggered: Boolean = false
  }

  def main(args: Array[String]) {
    val application = new ApplicationRunner(createScheduler, new TwitterApiService(), new InMemoryShelf(Seq.empty))
    application.startTwitterBot
  }
}
