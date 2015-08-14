package gr8craft.scheduling

class FakeScheduler extends Scheduler {
  var hour = 0
  var triggered = false

  def setTime(hour: Int, minute: Int): Any = {
    if (this.hour != hour) triggered = true
    this.hour = hour
  }

  override def isTriggered: Boolean = {
    val wasTriggered = triggered
    triggered = false
    wasTriggered
  }

}
