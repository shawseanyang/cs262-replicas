package com.chatapp.server;

import io.grpc.stub.StreamObserver;

// A thread-safe wrapper around StreamObserver that blocks threads that call onNext() until the current thread finishes executing onNext().

public class ConcurrentStreamObserver<T> {

  private StreamObserver<T> streamObserver;

  public ConcurrentStreamObserver(StreamObserver<T> streamObserver) {
    this.streamObserver = streamObserver;
  }

  public synchronized void onNext(T t) {
    streamObserver.onNext(t);
  }
}
