# gr8craft
A Twitter bot that aims to inspire you to create better software

[![Build Status](https://travis-ci.org/codurance/gr8craft.svg?branch=master)](https://travis-ci.org/codurance/gr8craft)

To run locally, create a file called application.conf in src/main/resources with the following content (replace the XXX with your values)

```
jdbc-connection {
  username = "XXX"
  password = "XXX"
  driverClassName = "XXX"
  url = "XXX"
}

twitter4j {
  consumerKey = XXX
  consumerSecret = XXX
  accessToken = XXX
  accessTokenSecret = XXX
}

twitter4jgr8craftmod {
  consumerKey = XXX
  consumerSecret = XXX
  accessToken = XXX
  accessTokenSecret = XXX
}

twitter4jgr8contributor {
  consumerKey = XXX
  consumerSecret = XXX
  accessToken = XXX
  accessTokenSecret = XXX
}
```
