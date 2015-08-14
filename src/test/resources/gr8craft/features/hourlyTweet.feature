Feature: Hourly Article Tweet
  As a follower of gr8craft
  I want hourly tweets to software design articles in my timeline
  so that they inspire me to do better design

  Scenario: Hour reached
    Given the clock shows 12:50
    Given the next article on the shelf about "DDD" can be found at "http://dddcommunity.org/book/nilsson_2006/"
    When the clock reaches 13:00
    Then gr8craft tweets "Your hourly recommended article about DDD: http://dddcommunity.org/book/nilsson_2006/"
