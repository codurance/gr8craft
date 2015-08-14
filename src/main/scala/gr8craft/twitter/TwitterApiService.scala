package gr8craft.twitter

class TwitterApiService() extends TwitterService {
  var tweet: String = null

  def getNewestTweet: String = tweet

  override def tweet(tweet: String): Unit = {
    this.tweet = tweet
    println(tweet)
  }
}
