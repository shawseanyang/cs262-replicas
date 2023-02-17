package com.chatapp.server;

// Represents a message that is waiting to be sent to a client

public class PendingMessage {
  private String recipient;
  private String message;

  public PendingMessage(String recipient, String message) {
    this.recipient = recipient;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getRecipient() {
    return recipient;
  }

}
