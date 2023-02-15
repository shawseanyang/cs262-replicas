package com.chatapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      // It is up to the client to determine whether to block the call
      // Here we create a blocking stub, but an async stub,
      // or an async stub with Future are always possible.
      TestGrpc.TestBlockingStub stub = TestGrpc.newBlockingStub(channel);
      TestOuterClass.TestRequest request =
        TestOuterClass.TestRequest.newBuilder()
          .setName("Ray")
          .build();

      // Finally, make the call using the stub
      TestOuterClass.TestResponse response = 
        stub.test(request);

      System.out.println(response);

      // A Channel should be shutdown before stopping the process.
      channel.shutdownNow();
    }
}
