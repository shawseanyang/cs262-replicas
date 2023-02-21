package com.chatapp.client.commands;

// A class that represents a command to list accounts

public class ListAccountsCommand implements Command {
  private String pattern;

  public ListAccountsCommand(String pattern) throws IllegalArgumentException {
    if (pattern == null || pattern.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    this.pattern = pattern;
  }

  public String getPattern() {
    return pattern;
  }
}
