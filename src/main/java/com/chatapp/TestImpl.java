package com.chatapp;

import com.chatapp.TestGrpc.TestImplBase;

import io.grpc.stub.StreamObserver;

public class TestImpl extends TestGrpc.TestImplBase {
  @Override
  public void test(TestOuterClass.TestRequest request,
        StreamObserver<TestOuterClass.TestResponse> responseObserver) {
  // HelloRequest has toString auto-generated.
    System.out.println(request);

    // You must use a builder to construct a new Protobuffer object
    TestOuterClass.TestResponse response = TestOuterClass.TestResponse.newBuilder()
      .setMessage("Hello there, " + request.getName())
      .build();

    // Use responseObserver to send a single response back
    responseObserver.onNext(response);

    // When you are done, you must call onCompleted.
    responseObserver.onCompleted();
  }
}
