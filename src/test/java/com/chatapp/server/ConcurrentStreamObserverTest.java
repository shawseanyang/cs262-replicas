package com.chatapp.server;

import org.junit.Test;

import io.grpc.stub.StreamObserver;

public class ConcurrentStreamObserverTest {
  // Create two threads that call onNext() on the same ConcurrentStreamObserver object at the same time and verify that the second thread blocks until the first thread finishes executing onNext(). This is done by making onNext() sleep for 1 second and then verifying that the total time taken by the two threads is strictly greater than 1 second.
  @Test
  public void onNext_blocksThreadsFromConcurrentlyCalling() {
    final ConcurrentStreamObserver<String> concurrentStreamObserver = new ConcurrentStreamObserver<String>(new StreamObserver<String>() {
      @Override
      public void onNext(String t) {
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
    });

    Thread thread1 = new Thread(new Runnable() {
      @Override
      public void run() {
        concurrentStreamObserver.onNext("hello");
      }
    });

    Thread thread2 = new Thread(new Runnable() {
      @Override
      public void run() {
        concurrentStreamObserver.onNext("hello");
      }
    });

    long startTime = System.currentTimeMillis();

    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();

    assert(endTime - startTime > 1000);
  }
}
