Feature: Receiving and reviewing submissions
  As a contributor to gr8craft
  I want to tweet new inspiration to gr8craft
  so that they can be given to a moderator to be reviewed for inclusion

  As a moderator for gr8craft
  I want to review received new submissions via Twitter direct message
  and approve them to be included in the hourly tweets


  Scenario: Accepting submissions
    Given "gr8craftmod" sends a DM to gr8craft with the text "inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8contributor"
    When the hour is reached
    Then gr8craft tweets "Your hourly recommended inspiration about DDD: http://t.co/lqJDZlGcJE (via @gr8contributor)"
