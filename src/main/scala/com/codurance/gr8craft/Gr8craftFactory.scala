package com.codurance.gr8craft

import akka.actor._
import com.codurance.gr8craft.infrastructure.TwitterApiService
import com.codurance.gr8craft.infrastructure.TwitterFactoryWithConfiguration.createTwitter
import com.codurance.gr8craft.model.inspiration.{Archivist, Inspiration, Shelf}
import com.codurance.gr8craft.model.publishing.{Publisher, TwitterService}
import com.codurance.gr8craft.model.research.{Journalist, Researcher}
import com.codurance.gr8craft.model.supervision.{Editor, Supervisor}

import scala.concurrent.duration._

object Gr8craftFactory {
  def createApplication(system: ActorSystem = ActorSystem("Gr8craftSystem"), twitterService: TwitterService = new TwitterApiService(createTwitter()), initialInspirations: Set[Inspiration] = Set.empty, tweetInterval: Duration = 1.hour): Gr8craft = {

    def createSupervisor(system: ActorSystem, twitterService: TwitterService, tweetInterval: Duration, archivist: ActorRef): ActorRef = {
      system.actorOf(Props(new Supervisor(tweetInterval, List(createEditor(archivist), createJournalist(archivist)))))
    }

    def createJournalist(archivist: ActorRef): ActorRef = {
      val researcher = system.actorOf(Props(new Researcher(twitterService)))
      system.actorOf(Props(new Journalist(researcher, archivist)))
    }

    def createEditor(archivist: ActorRef): ActorRef = {
      val publisher = system.actorOf(Props(new Publisher(twitterService)))
      system.actorOf(Props(new Editor(publisher, archivist)))
    }

    val archivist = system.actorOf(Props(new Archivist(new Shelf(initialInspirations))))
    new Gr8craft(createSupervisor(system, twitterService, tweetInterval, archivist))
  }
}
