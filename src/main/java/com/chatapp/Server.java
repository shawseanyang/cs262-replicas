package com.chatapp;

import io.grpc.ServerBuilder;

/**
 * Hello world!
 *
 */
public class Server 
{
    public static void main( String[] args ) throws Exception
    {
      // Create a new server to listen on port 8080
      io.grpc.Server server = ServerBuilder.forPort(8080)
        .addService(new TestImpl())
        .build();

      // Start the server
      server.start();

      // Server threads are running in the background.
      System.out.println("Server started");
      // Don't exit the main thread. Wait until server is terminated.
      server.awaitTermination();
    }
}
