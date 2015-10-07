package com.codurance.gr8craft.model.publishing

trait TweetSender {
  def tweet(tweet: Tweet, successAction: () => Unit): Unit
}
