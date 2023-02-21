package com.chatapp.client.commands;

// A class that represents a command to create an account

public class CreateAccountCommand implements Command {
  private String username;

  public CreateAccountCommand(String username) throws IllegalArgumentException {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
