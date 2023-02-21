package com.chatapp.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.chatapp.Chat.ChatMessage;

/**
 * Creates a thread that distributes messages to the client its responsible for
 * by entering a infinite loop that waits for messages to be added to the
 * blocking queue for that user. When a message is added to the queue, the
 * thread sends the message to the client.
 */

public class MessageDistributor extends Thread {

  private static final Logger logger = Logger.getLogger(MessageDistributor.class.getName());

  // This is used to stop the thread -- the infinite loop will end itself when this is set to false via the cease() method
  AtomicBoolean running = new AtomicBoolean(true);

  // The username of the user this MessageDistributor is responsible for and the observer used to send messages to the client provided by gRPC
  String username;
  ConcurrentStreamObserver<ChatMessage> observer;

  /**
   * Create a new MessageDistributor thread
   * 
   * @param username used to look up the blocking queue for the user
   * @param observer used to send messages to the client
   */
  public MessageDistributor(String username, ConcurrentStreamObserver<ChatMessage> observer) {
    this.username = username;
    this.observer = observer;
  }

  /**
   * Call this method to stop the thread
   */
  public void cease() {
    running.set(false);
  }

  /**
   * Call this method to start the thread
   */
  public void run() {
    while(running.get()) {
      // wait for a message to be added to the queue (blocking)
      PendingMessage message = BusinessLogicServer.getNextMessageFor(username);

      // double check again that this MessageDistributor should be running; if it shouldn't, then put the message back and end the thread
      if (!running.get()) {
        BusinessLogicServer.putMessageBackToDeliverLater(this.username, message);
        return;
      }

      // if the message is null, that means an interrupt occured, so just try again
      if (message == null) {
        continue;
      }

      // make sure the recipient is currently logged in;
      // if they aren't, then:
      // (1) put the message back on the front of the queue
      // (2) this thread should end itself because it no longer has an active 
      //     user to represent. This means that automatic message delivery will
      //     be paused until the user logs back in and a MessageDistributor is 
      //     started for them again.
      if (!BusinessLogicServer.isLoggedIn(this.username)) {
        logger.info("User " + this.username + " is not logged in, so its MessageDistributor thread will end itself.");
        BusinessLogicServer.putMessageBackToDeliverLater(this.username, message);
        return;
      }

      // distribute the message to the client
      String sender = message.getSender();
      String content = message.getMessage();
      observer.onNext(ChatMessageGenerator.DISTRIBUTE_MESSAGE(sender, content));
      logger.info("Sent message to " + username + " from " + sender + ": " + content);
    }
  }
}
