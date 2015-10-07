package com.codurance.gr8craft

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.codurance.gr8craft.infrastructure.TwitterApiService
import com.codurance.gr8craft.infrastructure.TwitterFactoryWithConfiguration.createTwitter
import com.codurance.gr8craft.model.inspiration.{Archivist, Inspiration, Shelf}
import com.codurance.gr8craft.model.publishing.{Publisher, TwitterService}
import com.codurance.gr8craft.model.supervision.{Editor, Supervisor}
import com.codurance.gr8craft.research.{Journalist, Researcher}

import scala.concurrent.duration._

object Gr8craftFactory {
  def createApplication(system: ActorSystem = ActorSystem("Gr8craftSystem"), twitterService: TwitterService = new TwitterApiService(createTwitter()), initialInspirations: Set[Inspiration] = Set.empty, tweetInterval: Duration = 1.hour): Gr8craft = {

    def createSupervisor(system: ActorSystem, twitterService: TwitterService, tweetInterval: Duration, archivist: ActorRef): ActorRef = {
      createActor(new Supervisor(tweetInterval, List(createEditor(archivist), createJournalist(archivist))))
    }

    def createJournalist(archivist: ActorRef): ActorRef = {
      val researcher = createActor(new Researcher(twitterService))
      createActor(new Journalist(researcher, archivist))
    }

    def createEditor(archivist: ActorRef): ActorRef = {
      val publisher = createActor(new Publisher(twitterService))
      createActor(new Editor(publisher, archivist))
    }

    def createActor(actor: Actor): ActorRef = {
      system.actorOf(Props(actor))
    }
    
    val archivist = createActor(new Archivist(new Shelf(initialInspirations)))
    new Gr8craft(createSupervisor(system, twitterService, tweetInterval, archivist))
  }
}
