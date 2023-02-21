package com.chatapp.client.commands;

// A class that represents a command to connect to a server

public class ConnectCommand implements Command {
  private String host;

  public ConnectCommand(String host) throws IllegalArgumentException {
    if (host == null || host.isEmpty()) {
      throw new IllegalArgumentException("host cannot be null or empty");
    }
    this.host = host;
  }

  public String getHost() {
    return host;
  }
}
