package gr8craft.scheduling

import java.util.concurrent.{Executors, TimeUnit}

class ScheduledExecutor extends Scheduler {
  var triggered: Boolean = false

  def scheduled: Runnable = new Runnable {
    override def run(): Unit = triggered = true
  }

  val executor = Executors.newSingleThreadScheduledExecutor()
  executor.schedule(scheduled, 1L, TimeUnit.HOURS)

  override def isTriggered: Boolean = {
    val wasTriggered = triggered
    triggered = false
    wasTriggered
  }


}
