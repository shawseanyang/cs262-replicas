package com.chatapp.server;

/*
 * A replica manager tells the current replica what its status is in a replica group and who the current leader is by running a leader election algorithm.
 */

public interface ReplicaManager extends Runnable {
  public Replica getSelf();
  public ReplicaStatus getStatus();
  public Replica getLeader();
  public boolean isLeader();
  public boolean isFollower();
}
