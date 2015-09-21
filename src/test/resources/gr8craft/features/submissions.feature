Feature: Receiving and reviewing submissions
  As a contributor to gr8craft
  I want to tweet new inspiration to gr8craft
  so that they can be given to a moderator to be reviewed for inclusion

  As a moderator for gr8craft
  I want to review received new submissions via Twitter direct message
  and approve them to be included in the hourly tweets

  Scenario: Receiving submissions
    Given The Bot receives a mention from "gr8craftmod" with the recommendation "my recommendation is about DDD at http://t.co/lqJDZlGcJE"
    When the hour is reached
    Then a moderator receives a DM from gr8craft saying "my recommendation is about DDD at http://t.co/lqJDZlGcJE - via gr8crafttest"

  Scenario: Accepting submissions
    Given a moderator sends a DM to "gr8crafttest" "inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8craftmod"
    When the hour is reached
    Then gr8craft tweets "Your hourly recommended inspiration about DDD: http://t.co/lqJDZlGcJE via @gr8craftmod"
