package com.codurance.gr8craft.model.research

class DirectMessageBuilder() {
  private var sender = "sender"
  private var text = "text"
  private var id = DirectMessageId(42)

  def withSender(sender: String): DirectMessageBuilder = {
    this.sender = sender
    this
  }

  def withText(text: String): DirectMessageBuilder = {
    this.text = text
    this
  }

  def withId(id: DirectMessageId): DirectMessageBuilder = {
    this.id = id
    this
  }

  def build(): DirectMessage = DirectMessage(sender, text, id)
}
