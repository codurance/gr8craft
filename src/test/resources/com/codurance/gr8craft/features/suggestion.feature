Feature: Receiving and reviewing suggestions
  As a contributor to gr8craft
  I want to tweet new inspiration to gr8craft
  so that they can be given to a moderator to be reviewed for inclusion

  As a moderator for gr8craft
  I want to review received new suggestions via Twitter direct message
  and approve them to be included in the hourly tweets

  Scenario: Accepting submissions
    Given "gr8craftmod" sends a DM to gr8craft with the text "inspiration: Technical Debt | location: https://t.co/ycPHNmQHUA | contributor: @gr8contributor"
    When the hour is reached
    Then gr8craft tweets "Your hourly recommended inspiration about Technical Debt: https://t.co/ycPHNmQHUA (via @gr8contributor)"
