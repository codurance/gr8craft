Feature: Hourly Inspiration Tweet
  As a follower of gr8craft
  I want hourly tweets to software design inspirations in my timeline
  so that they inspire me to do better design

  Scenario: Hour reached
    Given the next inspiration on the shelf about "DDD" can be found at "http://t.co/sLeXwc7FmO"
    When the hour is reached
    Then gr8craft tweets "Your hourly recommended inspiration about DDD: http://t.co/sLeXwc7FmO"
