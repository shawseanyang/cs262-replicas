package com.chatapp.server;

import java.util.ArrayList;

import com.chatapp.Chat.ChatMessage;

/**
 * A relay group is used by leaders to relay messages from the actual clients to the followers. One relay group should be created per actual client and contains enough relays to relay to each of the followers.
 */

public class RelayGroup {
  private final Relay[] relays;
  
  // Constructor: create a Relay for each of the replicas to relay to
  public RelayGroup(Replica[] targetReplicas) {
    // create a Relay for each follower
    ArrayList<Relay> r = new ArrayList<>();
    for (Replica follower : targetReplicas) {
      r.add(new Relay(follower));
    }
    relays = r.toArray(new Relay[r.size()]);
  }

  /*
   * Relays the given message to each replica in the group
   */
  public void relay(ChatMessage message) {
    for (Relay relay : relays) {
      relay.relay(message);
    }
  }

  /*
   * Closes the relays in the group
   */
  public void end() {
    for (Relay relay : relays) {
      relay.end();
    }
  }
}
