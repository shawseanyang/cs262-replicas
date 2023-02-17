package com.chatapp.server;

import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.grpc.ServerBuilder;
import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountRequest;
import com.chatapp.Chat.CreateAccountResponse;
import com.chatapp.Chat.DeleteAccountRequest;
import com.chatapp.Chat.DistributeMessageResponse;
import com.chatapp.Chat.ListAccountsRequest;
import com.chatapp.Chat.LogInRequest;
import com.chatapp.Chat.LogOutRequest;
import com.chatapp.Chat.SendMessageRequest;
import com.chatapp.protocol.Constant;
import com.google.rpc.Code;
import com.google.rpc.Status;

/**
 * Server that manages startup/shutdown of a {@code Chat} server.
 */
public class ChatServer {
  private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

  // The gRPC server object
  private Server server;

  /*
   * Maps a username to the thread that is handling the user's connection
   * If a username is mapped to null, that means the user is not logged in
   */
  private static HashMap<String, Thread> representativeThreads = new HashMap<String, Thread>();

  private static HashMap<String, BlockingQueue<PendingMessage>> pendingMessages = new HashMap<String, BlockingQueue<PendingMessage>>();

  private void start() throws IOException {
    server = ServerBuilder
        .forPort(Constant.PORT)
        .addService(new ChatServiceImpl()).build();

    logger.info("Server started, listening on " + Constant.PORT);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown
        // hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          ChatServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
    server.start();
  }

  private void stop() throws InterruptedException {
    if (server != null) {
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

  /**
   * Grab the next message for the user with the given username
   * @param username
   * @return the next message for the user or null if something went wrong
   */
  public static PendingMessage getNextMessageFor(String username) {
    try {
      return pendingMessages.get(username).take();
    } catch (InterruptedException e) {
      return null;
    }
  }

  /**
   * Implementation of {@code ChatService} that provides the handlers for the
   * server
   */
  static class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public StreamObserver<ChatMessage> chat(final StreamObserver<ChatMessage> responseObserver) {
      /**
       * Create a new StreamObserver that will handle the incoming messages from the
       * client, overriding the methods to provide the logic for each event. onNext()
       * is the default handler for normal messages.
       */
      return new StreamObserver<ChatMessage>() {
        @Override
        public void onNext(ChatMessage message) {
          // handle the message based on what type ("case") it is
          switch (message.getMessageCase()) {
            case CREATE_ACCOUNT_REQUEST: {
              String username = message.getCreateAccountRequest().getUsername();
              // respond with an exception if the username is already taken
              if (representativeThreads.containsKey(username)) {
                logger.info(
                  "Failed to create account for " + username + " because the username is already taken"
                );
                responseObserver.onNext(
                  ChatMessageGenerator
                    .CREATE_ACCOUNT_USER_ALREADY_EXISTS(username));
                return;
              }
              // add entries to representativeThreads and pendingMessages for the new user initialized to null and empty, respectively
              representativeThreads.put(username, null);
              pendingMessages.put(
                username,
                new LinkedBlockingDeque<PendingMessage>()
              );
              logger.info("Created account for " + username);
              // respond with a success message
              responseObserver.onNext(
                ChatMessageGenerator.CREATE_ACCOUNT_SUCCESS(username));
              break;
            }
            case LOG_IN_REQUEST: {
              String username = message.getLogInRequest().getUsername();
              // respond with an exception if the username does not exist
              if (!representativeThreads.containsKey(username)) {
                logger.info(
                  "Failed to log in " + username + " because the username does not exist"
                );
                responseObserver.onNext(
                  ChatMessageGenerator
                    .LOG_IN_USER_DOES_NOT_EXIST(username));
                return;
              }
              // create a new thread to handle the user's connection
              Thread md = new MessageDistributor(username, responseObserver);
              md.start();
              
              logger.info("Logged in " + username);
              // respond with a success message
              responseObserver.onNext(
                ChatMessageGenerator.LOG_IN_SUCCESS(username));
              break;
            }
            case LOG_OUT_REQUEST: {
              // TODO
              LogOutRequest request = message.getLogOutRequest();
              break;
            }
            case SEND_MESSAGE_REQUEST: {
              // TODO
              SendMessageRequest request = message.getSendMessageRequest();
              break;
            }
            case LIST_ACCOUNTS_REQUEST: {
              // TODO
              ListAccountsRequest request = message.getListAccountsRequest();
              break;
            }
            case DELETE_ACCOUNT_REQUEST: {
              // TODO
              DeleteAccountRequest request = message.getDeleteAccountRequest();
              break;
            }
            case DISTRIBUTE_MESSAGE_RESPONSE: {
              // TODO
              DistributeMessageResponse response = message.getDistributeMessageResponse();
              break;
            }
            default:
              break;
          }
        }

        @Override
        public void onError(Throwable t) {
          logger.log(Level.WARNING, "Encountered error in routeChat", t);
        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();
        }
      };
    }
  }
}