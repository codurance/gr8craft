package com.codurance.gr8craft

import akka.actor._
import com.codurance.gr8craft.infrastructure.{DirectMessageFetcherViaTwitter, TweetSenderViaTwitter}
import com.codurance.gr8craft.infrastructure.TwitterFactoryWithConfiguration.createTwitter
import com.codurance.gr8craft.model.inspiration.{Archivist, Inspiration, Shelf}
import com.codurance.gr8craft.model.publishing.{DirectMessageFetcher, Publisher, TweetSender}
import com.codurance.gr8craft.model.research.{Journalist, Researcher}
import com.codurance.gr8craft.model.supervision.{Editor, Supervisor}

import scala.concurrent.duration._

object Gr8craftFactory {
  def createApplication(system: ActorSystem = ActorSystem("Gr8craftSystem"), tweetSender: TweetSender = new TweetSenderViaTwitter(createTwitter()), directMessageFetcher: DirectMessageFetcher = new DirectMessageFetcherViaTwitter(createTwitter()), initialInspirations: Set[Inspiration] = Set.empty, tweetInterval: Duration = 1.hour): Gr8craft = {

    def createSupervisor(system: ActorSystem, twitterService: TweetSender, tweetInterval: Duration, archivist: ActorRef): ActorRef = {
      system.actorOf(Props(new Supervisor(tweetInterval, List(createEditor(archivist), createJournalist(archivist)))))
    }

    def createJournalist(archivist: ActorRef): ActorRef = {
      val researcher = system.actorOf(Props(new Researcher(directMessageFetcher)))
      system.actorOf(Props(new Journalist(researcher, archivist)))
    }

    def createEditor(archivist: ActorRef): ActorRef = {
      val publisher = system.actorOf(Props(new Publisher(tweetSender)))
      system.actorOf(Props(new Editor(publisher, archivist)))
    }

    val archivist = system.actorOf(Props(new Archivist(new Shelf(initialInspirations))))
    new Gr8craft(createSupervisor(system, tweetSender, tweetInterval, archivist))
  }
}
