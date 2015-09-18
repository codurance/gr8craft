package gr8craft.twitter

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.Inspiration
import gr8craft.messages._

case object Triggered

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

class TweetRunner(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {

  override def persistenceId: String = "TweetRunner"

  override def receiveRecover: Receive = {
    case Triggered => shelf ! Skip
    case Tweeted(inspiration) =>
    case Added(inspiration) => shelf ! AddInspiration(inspiration)
  }

  override def receiveCommand: Receive = {
    case Trigger => persist(Triggered)(_ => shelf ! InspireMe)
    case AddInspiration(inspiration) => addInspiration(inspiration)
    case Inspire(inspiration) => tweet(inspiration)
  }

  def addInspiration(inspiration: Inspiration): Unit = {
    persist(Added(inspiration))(_ =>
      shelf ! AddInspiration(inspiration))
  }

  def tweet(inspiration: Inspiration): Unit = {
    persist(Tweeted(inspiration))(_ =>
      tweeter ! Tweet(s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}")
    )
  }
}
