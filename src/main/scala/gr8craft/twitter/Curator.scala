package gr8craft.twitter

import java.time.LocalDateTime
import java.time.LocalDateTime.MIN

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import gr8craft.scheduling.Clock

case class Triggered(lastFetched: Long)

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

case class ReadDirectMessage(id: Long)

class Curator(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {
  var lastFetched = 0L

  override def persistenceId: String = "Curator"

  override def receiveRecover: Receive = {
    case Triggered(lastFetched: Long) =>
      shelf ! Skip
      this.lastFetched = lastFetched
    case Tweeted(inspiration) =>
    case Added(inspiration) => shelf ! AddInspiration(inspiration)
    case ReadDirectMessage(id) => lastFetched = id
  }

  override def receiveCommand: Receive = {
    case Trigger => triggerRegularActions()
    case AddInspiration(inspiration) => addInspiration(inspiration)
    case Inspire(inspiration) => tweet(inspiration)
    case GotDirectMessage(directMessage) => gotDirectMessage(directMessage)
  }

  private def gotDirectMessage(directMessage: DirectMessage): Unit = {
    persist(ReadDirectMessage(directMessage.id))(_ => {
      lastFetched = directMessage.id
    })
  }

  private def triggerRegularActions(): Unit = {
    persist(Triggered(lastFetched))(_ => {
      shelf ! InspireMe
      tweeter ! FetchDirectMessages(lastFetched)
    })
  }

  private def addInspiration(inspiration: Inspiration): Unit = {
    persist(Added(inspiration))(_ =>
      shelf ! AddInspiration(inspiration))
  }

  private def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      tweeter ! GoAndTweet(inspiration)
    )
  }
}
