package gr8craft.scheduling

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit

class ScheduledExecutor(timeUnit: TimeUnit, runnable: Runnable) extends Scheduler {
  def schedule() {
    newSingleThreadScheduledExecutor().schedule(runnable, 1L, timeUnit)
  }
}
