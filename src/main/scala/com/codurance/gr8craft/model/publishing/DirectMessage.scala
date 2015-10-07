package com.codurance.gr8craft.model.publishing

case class DirectMessageId(id: Long)

case class DirectMessage(sender: String, text: String, id: DirectMessageId)
