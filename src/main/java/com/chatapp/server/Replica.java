package com.chatapp.server;

/**
 * Represents a replica, which is a replication of the chat app server. A replica has an ID, an IP address and a port number. This file also lists the replicas. The ID determines the precedence of the replica. The replica with the highest ID is the leader.
 */

public class Replica {
  private int id;
  private String ipAddress;
  private int portNumber;

  private Replica(int id, String ipAddress, int portNumber) {
    this.ipAddress = ipAddress;
    this.portNumber = portNumber;
  }

  public int getId() {
    return id;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public int getPortNumber() {
    return portNumber;
  }

  /*
   * The list of all the replicas in descending order of precedence.
   */
  public static final Replica[] REPLICAS = {
    new Replica(2, "localhost", 8000),
    new Replica(1, "localhost", 8001),
    new Replica(0, "localhost", 8002)
  };

  /*
   * Returns a list of all the replicas that have a higher ID than the given replica in descending order.
   */
  public static Replica[] getHigherUps(Replica replica) {
    Replica[] higherUps = new Replica[replica.getId() - 1];
    for (int i = 0; i < replica.getId() - 1; i++) {
      higherUps[i] = REPLICAS[i];
    }
    return higherUps;
  }

  /*
   * Returns the matching replica given the ID.
   * 
   * @throws IllegalArgumentException if the ID is invalid
   */
  public static Replica getReplicaById(int id) {
    for (Replica replica : REPLICAS) {
      if (replica.getId() == id) {
        return replica;
      }
    }
    throw new IllegalArgumentException("Invalid ID");
  }
}