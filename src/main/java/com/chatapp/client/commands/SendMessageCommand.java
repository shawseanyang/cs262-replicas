package com.chatapp.client.commands;

// A class that represents a command to send a message to a user

public class SendMessageCommand implements Command {
  private String recipient;
  private String message;

  public SendMessageCommand(String recipient, String message) throws IllegalArgumentException {
    if (recipient == null || recipient.isEmpty()) {
      throw new IllegalArgumentException("Recipient cannot be null or empty");
    }
    if (message == null || message.isEmpty()) {
      throw new IllegalArgumentException("Message cannot be null or empty");
    }
    this.recipient = recipient;
    this.message = message;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getMessage() {
    return message;
  }
}
