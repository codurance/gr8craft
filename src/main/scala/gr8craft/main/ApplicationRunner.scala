package gr8craft.main

import gr8craft.article.Shelf
import gr8craft.scheduling.Scheduler
import gr8craft.twitter.{TwitterApiService, TwitterService}

class ApplicationRunner(scheduler: Scheduler, twitterService: TwitterService, shelf: Shelf) {
  def startTwitterBot() = {}

  def stop = {}

}

object ApplicationRunner {

  def createScheduler: Scheduler = new Scheduler {
    override def isTriggered: Boolean = false
  }

  def main(args: Array[String]) {
    val application = new ApplicationRunner(createScheduler, new TwitterApiService(), new Shelf(Seq.empty))
    application.startTwitterBot
  }
}
