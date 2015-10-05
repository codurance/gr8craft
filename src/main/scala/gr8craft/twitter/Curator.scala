package gr8craft.twitter

import java.time.LocalDateTime
import java.time.LocalDateTime.MIN

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import gr8craft.scheduling.Clock

case class Triggered(timestamp: LocalDateTime)

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

class Curator(tweeter: ActorRef, shelf: ActorRef, clock: Clock) extends PersistentActor {
  var lastTriggered = MIN

  override def persistenceId: String = "Curator"

  override def receiveRecover: Receive = {
    case Triggered(timestamp: LocalDateTime) =>
      shelf ! Skip
      lastTriggered = timestamp
    case Tweeted(inspiration) =>
    case Added(inspiration) => shelf ! AddInspiration(inspiration)
  }

  override def receiveCommand: Receive = {
    case Trigger => triggerRegularActions()
    case AddInspiration(inspiration) => addInspiration(inspiration)
    case Inspire(inspiration) => tweet(inspiration)
  }

  def triggerRegularActions(): Unit = {
    persist(Triggered(clock.now))(_ => {
      shelf ! InspireMe
      tweeter ! FetchDirectMessages(lastTriggered)
      lastTriggered = clock.now
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
