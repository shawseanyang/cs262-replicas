package com.chatapp.client;

import com.chatapp.protocol.Server;

/**
 * Manages the set of servers that the client can connect to. The main purpose of this class is to keep track of the current server that the client is connected to and which server to try to connect to next if the current one fails.
 */

public class ServerManager {
  private Server current;
  private int index = 0;

  public ServerManager() {
    current = Server.SERVERS[index];
    index += 1;
  }

  public Server getCurrent() {
    return current;
  }

  public Server getNext() {
    // wrap the index around if it goes out of bounds
    if (index >= Server.SERVERS.length) {
      index = 0;
    }
    current = Server.SERVERS[index];
    index += 1;
    return current;
  }
}
