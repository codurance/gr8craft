Feature: Hourly Article Tweet
  As a follower of gr8craft
  I want hourly tweets to software design articles in my timeline
  so that they inspire me to do better design

  Scenario: Hour reached
    Given the next article on the shelf can be found at 'http://dddcommunity.org/book/nilsson_2006/'
    When the clock reaches 1 pm
    gr8craft tweets 'http://dddcommunity.org/book/nilsson_2006/'
