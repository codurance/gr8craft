package com.codurance.gr8craft.model.twitter

case class DirectMessageId(id: Long)

case class DirectMessage(sender: String, text: String, id: DirectMessageId)
