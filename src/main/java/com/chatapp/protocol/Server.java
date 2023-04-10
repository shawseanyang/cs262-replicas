package com.chatapp.protocol;

/**
 * Represents a server that can be connected to by a client.
 */

public class Server {
  String address;
  int port;

  public Server(String address, int port) {
    this.address = address;
    this.port = port;
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return address + ":" + port;
  }

  /*
   * The list of all the servers in the system.
   */
  public final static Server[] SERVERS = {
    new Server("localhost", 8000),
    new Server("localhost", 8001),
    new Server("localhost", 8002)
  };
}
