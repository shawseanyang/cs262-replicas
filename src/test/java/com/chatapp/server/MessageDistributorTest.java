package com.chatapp.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chatapp.Chat.ChatMessage;

import io.grpc.stub.StreamObserver;

// Tests the message distributor by mocking the observer and the blocking queue

public class MessageDistributorTest {

  // Create a thread that calls run() on a MessageDistributor object and verify that the thread does not block. This is done by making run() sleep for 1 second and then verifying that the total time taken by the thread is strictly less than 1 second.
  @Test
  public void run_doesNotBlock() {
    final MessageDistributor messageDistributor = new MessageDistributor("username", new ConcurrentStreamObserver<ChatMessage>(new StreamObserver<ChatMessage>() {
      @Override
      public void onNext(ChatMessage t) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onError(Throwable t) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onCompleted() {
        // TODO Auto-generated method stub
        
      }
    }));

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        messageDistributor.run();
      }
    });

    long startTime = System.currentTimeMillis();

    thread.start();

    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();

    assertTrue(endTime - startTime < 1000);
  }
}
