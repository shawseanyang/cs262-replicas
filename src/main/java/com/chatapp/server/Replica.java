package com.chatapp.server;

import java.util.ArrayList;

import com.chatapp.protocol.Server;

/**
 * Represents a replica, which is a replication of the chat app server. A replica has an ID, an IP address and a port number. This file also lists the replicas. The ID determines the precedence of the replica. The replica with the highest ID is the leader.
 */

public class Replica {
  private int id;
  private String ipAddress;
  private int portNumber;

  private Replica(int id, String ipAddress, int portNumber) {
    this.id = id;
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
    new Replica(2, "localhost", 8080),
    new Replica(1, "localhost", 8081),
    new Replica(0, "localhost", 8082)
  };

  /*
   * Returns the business logic server that corresponds to this replica
   */
  public static Server getServer(Replica replica) {
    // find the index of the replica in the list of replicas
    int index = -1;
    for (int i = 0; i < REPLICAS.length; i++) {
      if (REPLICAS[i].equals(replica)) {
        index = i;
        break;
      }
    }
    // if the replica is not in the list, throw an exception
    if (index == -1) {
      throw new IllegalArgumentException("Invalid replica");
    }
    // return the corresponding server
    return Server.SERVERS[index];
  }

  /*
   * Returns a list of all the replicas that have a higher ID than the given replica in descending order.
   */
  public static Replica[] getHigherUps(Replica replica) {
    ArrayList<Replica> higherUps = new ArrayList<Replica>();
    for (Replica r : REPLICAS) {
      if (r.getId() > replica.getId()) {
        higherUps.add(r);
      }
    }
    return higherUps.toArray(new Replica[higherUps.size()]);
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
  
  /*
   * Returns a list of all the replicas except the one given.
   */
  public static Replica[] getOthers(Replica replica) {
    ArrayList<Replica> others = new ArrayList<Replica>();
    for (Replica r : REPLICAS) {
      if (r.getId() != replica.getId()) {
        others.add(r);
      }
    }
    return others.toArray(new Replica[others.size()]);
  } 

  /*
   * Returns the ID's of the given replicas.
   */
  public static String getIdsAsString(Replica[] replicas) {
    String result = "";
    for (Replica replica : replicas) {
      result += replica.getId() + " ";
    }
    return result;
  }
}