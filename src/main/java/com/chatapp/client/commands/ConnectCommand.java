package com.chatapp.client.commands;

public class ConnectCommand implements Command {
  private String host;
  private int port;

  private static final int MIN_PORT = 0;
  private static final int MAX_PORT = 65535;

  public ConnectCommand(String host, int port) throws IllegalArgumentException {
    if (host == null || host.isEmpty()) {
      throw new IllegalArgumentException("host cannot be null or empty");
    }
    if (port < MIN_PORT || port > MAX_PORT) {
      throw new IllegalArgumentException("port must be between 0 and 65535");
    }
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
