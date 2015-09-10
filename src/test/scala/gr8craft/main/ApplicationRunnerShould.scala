import akka.actor.{ActorSystem, ActorRef}
import akka.testkit.JavaTestKit
import gr8craft.ApplicationRunner
import gr8craft.messages.{Stop, Start}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ApplicationRunnerShould extends FunSuite with MockFactory {
  val system = ActorSystem("ApplicationRunnerShould")
  val scheduler = new JavaTestKit(system)
  val applicationRunner = new ApplicationRunner(scheduler.getRef)

  test("schedule the execution") {
    applicationRunner.startTwitterBot()

    scheduler.expectMsgEquals(1.second, Start)
  }

  test("stop the execution") {
    applicationRunner.stop()

    scheduler.expectMsgEquals(1.second, Stop)
  }
}
