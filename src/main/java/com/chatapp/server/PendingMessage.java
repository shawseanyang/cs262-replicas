package com.chatapp.server;

// Represents a message that is waiting to be sent to a client

public class PendingMessage {
  private String recipient;
  private String sender;
  private String message;

  public PendingMessage(String recipient, String sender, String message) {
    this.recipient = recipient;
    this.sender = sender;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getSender() {
    return sender;
  }

  public String getRecipient() {
    return recipient;
  }

}
