package gr8craft.twitter

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.Inspiration
import gr8craft.messages._

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

class TweetRunner(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {

  override def persistenceId: String = "TweetRunner"

  override def receiveRecover: Receive = {
    case Trigger => shelf ! Next
    case AddInspiration(inspiration) => shelf ! AddInspiration(inspiration)
    case Inspire(inspiration) =>
  }

  override def receiveCommand: Receive = {
    case Trigger => shelf ! Next
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
