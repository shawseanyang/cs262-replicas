package com.chatapp.server;

import java.util.HashMap;

import com.chatapp.BullyServiceGrpc;
import com.chatapp.Bully.Empty;
import com.chatapp.BullyServiceGrpc.BullyServiceBlockingStub;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;

/**
 * The Bully algorithm for leader election.
 */

public class Bully implements Runnable {
  /* The identity of this replica. */
  final Replica self;

  /* The current leader of the replica group. Starts as no one. */
  Replica leader = null;

  /* The stubs of the other replicas. */
  final HashMap<Replica, BullyServiceBlockingStub> stubs;

  /* The gRPC server for this replica */
  final Server server;

  /* Constructor takes an identity `me` for this replica, initializes the stubs of the other replicas, and sets up the server. */
  public Bully (Replica me) {
    self = me;
    stubs = createStubs();
    server = createServer();
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
   * Creates the stubs to the other replicas.
   */
  private HashMap<Replica, BullyServiceBlockingStub> createStubs() {
    HashMap<Replica, BullyServiceBlockingStub> stubs = new HashMap<>();
    for (Replica replica : Replica.REPLICAS) {
      if (replica != self) {
        stubs.put(replica, BullyServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(replica.getIpAddress(), replica.getPortNumber()).usePlaintext().build()));
      }
    }
    return stubs;
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
      if (getReplicaStatus() != ReplicaStatus.LEADER) {
        // If the leader is null or if the leader is dead, then elect a new leader.
        if (leader == null || !isAlive(leader)) {
          // Elect a new leader
          leader = electLeader();
          // If this replica has been elected as the leader, then delcare victory
          if (getReplicaStatus() == ReplicaStatus.LEADER) {
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
  public ReplicaStatus getReplicaStatus() {
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
    for (Replica higherUp : higherUps) {
      if (isAlive(higherUp)) {
        // the first higher up that is alive is the leader
        return higherUp;
      }
    }
    // if there are no higher ups that are alive, then this replica is the leader
    return self;
  }

  /*
   * Returns true if the given replica is alive.
   */
  private boolean isAlive(Replica replica) {
    // Try to ping the replica
    try {
      stubs.get(replica).ping(null);
    } catch (StatusRuntimeException e) {
      return false;
    }
    return true;
  }

  /*
   * Declares this replica as the new leader to all other replicas.
   */
  private void declareVictory() {
    for (Replica replica : Replica.REPLICAS) {
      try {
        stubs.get(replica).declareVictory(convertSelfToProto());
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
      leader = Replica.getReplicaById(request.getId());
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }
  }
}
