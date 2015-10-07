package com.codurance.gr8craft.model.research

case class DirectMessageId(id: Long)

case class DirectMessage(sender: String, text: String, id: DirectMessageId)
