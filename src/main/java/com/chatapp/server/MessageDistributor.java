package com.chatapp.server;

import java.util.logging.Logger;

import com.chatapp.Chat.ChatMessage;

import io.grpc.stub.StreamObserver;

/**
 * Creates a thread that distributes messages to the client its responsible for
 * by entering a infinite loop that waits for messages to be added to the
 * blocking queue for that user. When a message is added to the queue, the
 * thread sends the message to the client.
 */

public class MessageDistributor extends Thread {

  private static final Logger logger = Logger.getLogger(MessageDistributor.class.getName());

  String username;
  StreamObserver<ChatMessage> observer;

  /**
   * Create a new MessageDistributor thread
   * 
   * @param username used to look up the blocking queue for the user
   * @param observer used to send messages to the client
   */
  public MessageDistributor(String username, StreamObserver<ChatMessage> observer) {
    this.username = username;
    this.observer = observer;
  }

  /**
   * Call this method to start the thread
   */
  public void run() {
    while(true) {
      // wait for a message to be added to the queue
      PendingMessage message = ChatServer.getNextMessageFor(username);
      if (message == null) {
        // if the message is null, that means an interrupt occured, so just try again
        continue;
      }
      String sender = message.getSender();
      String content = message.getMessage();
      // send the message to the client
      observer.onNext(ChatMessageGenerator.DISTRIBUTE_MESSAGE(sender, content));
      logger.info("Sent message to " + username + " from " + sender + ": " + content);
    }
  }
}
