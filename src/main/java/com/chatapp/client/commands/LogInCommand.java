package com.chatapp.client.commands;

// A class that represents a command to log in to the server

public class LogInCommand implements Command {
  private String username;

  public LogInCommand(String username) throws IllegalArgumentException {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
