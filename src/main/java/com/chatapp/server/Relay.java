package com.chatapp.server;

import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.ChatMessage;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Relays are used by leaders to relay messages from the actual clients to the followers. It establishes a new client with the replica that it is relaying to.
 */

public class Relay {

  private final StreamObserver<ChatMessage> observer;

  public Relay (Replica replica) {
    com.chatapp.protocol.Server server = Replica.getServer(replica);
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress(server.getAddress(), server.getPort())
      .usePlaintext()
      .build();
    ChatServiceStub stub = ChatServiceGrpc.newStub(channel);
    observer = stub.chat(new StreamObserver<ChatMessage>() {
      // The leader doesn't do anything with the followers' responses.
      @Override
      public void onNext(ChatMessage value) {}

      @Override
      public void onError(Throwable t) {
        System.out.println("relay on error: " + t.getMessage());
      }

      @Override
      public void onCompleted() {}
    });
  }

  /*
   * Relays the given message to the follower
   */
  public void relay(ChatMessage message) {
    System.out.println("Relaying message to follower.");
    observer.onNext(message);
  }

  /*
   * Ends the relay
   */
  public void end() {
    observer.onCompleted();
  }
}
