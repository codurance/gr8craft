package gr8craft.scheduling

import java.time.Clock.systemDefaultZone
import java.time.LocalDateTime

class Clock(clock: java.time.Clock = systemDefaultZone()) {
  def now = LocalDateTime.now(clock)
}
