package com.chatapp.server;

import com.chatapp.BullyServiceGrpc;
import com.chatapp.Bully.Empty;
import com.chatapp.BullyServiceGrpc.BullyServiceBlockingStub;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;

/**
 * The Bully algorithm for leader election is a way of managing replicas in a replica group.
 */

public class Bully implements ReplicaManager {
  /* The identity of this replica. */
  final Replica self;

  /* The current leader of the replica group. Starts as no one. Should be set using the given setter if console messages are desired. */
  Replica leader = null;

  /* The gRPC server for this replica */
  final Server server;

  /* Constructor takes an identity `me` for this replica, initializes the stubs of the other replicas, and sets up the server. */
  public Bully (Replica me) {
    System.out.println("I am " + me.getId() + ".");
    self = me;
    server = createServer();
  }

  /*
   * Returns the identity of this replica.
   */
  public Replica getSelf() {
    return self;
  }

  /*
   * Setter for the leader variable. Prints a message to the console to indicate the change.
   */
  public void setLeader(Replica newLeader) {
    System.out.println("Leader is now " + newLeader.getId() + ".");
    leader = newLeader;
  }

  /*
   * Getter for the leader variable.
   */
  public Replica getLeader() {
    return leader;
  }

  /*
   * Creates the server for this replica.
   */
  private Server createServer() {
    return ServerBuilder
      .forPort(self.getPortNumber())
      .addService(new BullyServiceImpl())
      .build();
  }

  /*
   * Returns whether this replica is the leader.
   */
  public boolean isLeader() {
    return getStatus() == ReplicaStatus.LEADER;
  }

  /*
   * Returns whether this replica is a follower.
   */
  public boolean isFollower() {
    return getStatus() == ReplicaStatus.FOLLOWER;
  }

  /**
   * Returns a gRPC stub for the given replica.
   */
  public BullyServiceBlockingStub getStubFor(Replica replica) {
    return BullyServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(replica.getIpAddress(), replica.getPortNumber()).usePlaintext().build());
  }

  /*
   * Runs the Bully algorithm
   */
  public void run() {
    // Start the server
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Enter the client-side loop
    while(true) {
      // If this replica is a leader, do nothing. Otherwise, ping the leader to see if its still alive.
      if (getStatus() != ReplicaStatus.LEADER) {
        // If the leader is null or if the leader is dead, then elect a new leader.
        if (leader == null || !isAlive(leader)) {
          // Elect a new leader
          Replica candidate = electLeader();
          // If this replica believes it should be the leader, then become the leader and declare victory to all other replicas.
          if (candidate.equals(self)) {
            setLeader(self);
            declareVictory();
          }
        }
      }

      // sleep for 1 second
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Returns the status of this replica.
   */
  public ReplicaStatus getStatus() {
    if (leader == null) {
      return ReplicaStatus.FOLLOWER;
    } else if (leader.equals(self)) {
      return ReplicaStatus.LEADER;
    } else {
      return ReplicaStatus.FOLLOWER;
    }
  }

  /*
   * Runs a leader election and returns the result.
   */
  private Replica electLeader() {
    // Ping all of the replicas with a higher ID than this replica in order of precedence.
    Replica[] higherUps = Replica.getHigherUps(self);
    //System.out.println("My higher ups are " + Replica.getIdsAsString(higherUps) + ".");
    for (Replica higherUp : higherUps) {
      if (isAlive(higherUp)) {
        // the first higher up that is alive is the leader
        //System.out.println("Elected " + higherUp.getId() + " as the new leader.");
        return higherUp;
      }
    }
    // if there are no higher ups that are alive, then this replica is the leader
    //System.out.println("Elected myself as the new leader.");
    return self;
  }

  /*
   * Returns true if the given replica is alive.
   */
  private boolean isAlive(Replica replica) {
    // Try to ping the replica
    try {
      getStubFor(replica).ping(null);
    } catch (StatusRuntimeException e) {
      // System.out.println("Replica " + replica.getId() + " is dead.");
      return false;
    }
    // System.out.println("Replica " + replica.getId() + " is alive.");
    return true;
  }

  /*
   * Declares this replica as the new leader to all other replicas.
   */
  private void declareVictory() {
    System.out.println("I am the new leader!");
    for (Replica replica : Replica.getOthers(self)) {
      try {
        getStubFor(replica).declareVictory(convertSelfToProto());
      } catch (StatusRuntimeException e) {
        // ignore
      }
    }
  }

  /*
   * Returns a proto representation of this replica.
   */
  private com.chatapp.Bully.Replica convertSelfToProto() {
    return com.chatapp.Bully.Replica.newBuilder().setId(self.getId()).build();
  }

  /*
   * The gRPC service implementation for this replica. This is the "server" side code.
   */
  private class BullyServiceImpl extends BullyServiceGrpc.BullyServiceImplBase {
    /*
     * When this replica gets a ping, simply respond with an empty message.
     */
    @Override
    public void ping(Empty request, io.grpc.stub.StreamObserver<Empty> responseObserver) {
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }

    /*
     * When this replica gets a declaration of victory from another replica, it should update its leader accordingly.
     */
    @Override
    public void declareVictory(com.chatapp.Bully.Replica request, io.grpc.stub.StreamObserver<Empty> responseObserver) {
      setLeader(Replica.getReplicaById(request.getId()));
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }
  }
}
