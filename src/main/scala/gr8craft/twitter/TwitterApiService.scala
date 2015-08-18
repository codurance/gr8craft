package gr8craft.twitter

import twitter4j.Twitter

class TwitterApiService(twitter: Twitter) extends TwitterService {
  var tweet: String = null

  override def tweet(tweet: String) {
    twitter.updateStatus(tweet)
  }
}
