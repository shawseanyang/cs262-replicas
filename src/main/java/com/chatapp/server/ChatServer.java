package com.chatapp.server;

import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grpc.ServerBuilder;
import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.ChatMessage;
import com.chatapp.protocol.Constant;

/**
 * Server that manages startup/shutdown of a {@code Chat} server. This class 
 * manages the lifecycle of a server, whereas the actual business logic is 
 * handled by the {@code BusinessLogicServer} class.
 */
public class ChatServer {
  private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

  // The gRPC server object, to be populated by start()
  private Server server;

  /**
   * Start serving requests.
   * @throws IOException
   */
  private void start() throws IOException {
    // Start the business logic portion of the server
    BusinessLogicServer businessLogicServer = new BusinessLogicServer();

    // Then grab the gRPC server that it creates
    server = businessLogicServer.getServer();

    logger.info("Server started, listening on " + Constant.PORT);

    // Add a shutdown hook to the JVM so that the server can be shut down gracefully
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        // Shut down the server
        try {
          ChatServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });

    // Actually start the gRPC server
    server.start();
  }

  /**
   * Stop serving requests and shutdown resources.
   * @throws InterruptedException
   */
  private void stop() throws InterruptedException {
    if (server != null) {
      // wait for the server to terminate for at most 30 seconds
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon
   * threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final ChatServer server = new ChatServer();
    server.start();
    server.blockUntilShutdown();
  }
}