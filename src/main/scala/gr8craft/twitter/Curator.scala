package gr8craft.twitter

import akka.actor.ActorRef
import akka.persistence.PersistentActor
import gr8craft.inspiration.Inspiration
import gr8craft.messages._

case class Triggered(lastFetched: Option[Long])

case class Tweeted(inspiration: Inspiration)

case class Added(inspiration: Inspiration)

case class ReadDirectMessage(id: Long)

class Curator(tweeter: ActorRef, shelf: ActorRef) extends PersistentActor {
  var lastFetched: Option[Long] = None

  override def persistenceId: String = "Curator"

  override def receiveRecover: Receive = {
    case Triggered(id) =>
      shelf ! Skip
      this.lastFetched = id
    case Tweeted(inspiration) =>
    case Added(inspiration) =>
      shelf ! AddInspiration(inspiration)
    case ReadDirectMessage(id) =>
      lastFetched = Some(id)
  }

  override def receiveCommand: Receive = {
    case Trigger =>
      triggerRegularActions()
    case AddInspiration(inspiration) =>
      addInspiration(inspiration)
    case Inspire(inspiration) =>
      tweet(inspiration)
    case GotDirectMessage(directMessage) =>
      gotDirectMessage(directMessage)
  }

  private def gotDirectMessage(directMessage: DirectMessage): Unit = {
    persist(ReadDirectMessage(directMessage.id))(_ => {
      lastFetched = Some(directMessage.id)
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
