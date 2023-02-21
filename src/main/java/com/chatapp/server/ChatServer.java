package com.chatapp.server;

import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grpc.ServerBuilder;
import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.DeleteAccountRequest;
import com.chatapp.Chat.DistributeMessageResponse;
import com.chatapp.Chat.ListAccountsRequest;
import com.chatapp.protocol.Constant;
import com.google.protobuf.Message;

/**
 * Server that manages startup/shutdown of a {@code Chat} server.
 */
public class ChatServer {
  private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

  // The gRPC server object
  private Server server;

  /**
   * Associates users to their MessageDistributors, which is a proxy for
   * whether the user is logged in. Uses Optional to allow for "null" values,
   * representing a user that is not logged in.
   */
  private static ConcurrentHashMap<String, Optional<MessageDistributor>> messageDistributors = new ConcurrentHashMap<String, Optional<MessageDistributor>>();

  // An empty message distributor for use with the ConcurrentHashMap
  private static final Optional<MessageDistributor> EMPTY_MESSAGE_DISTRIBUTOR = Optional.empty();

  /**
   * Tracks the messages that are waiting to be sent to each user
   */
  private static ConcurrentHashMap<String, BlockingDeque<PendingMessage>> pendingMessages = new ConcurrentHashMap<String, BlockingDeque<PendingMessage>>();

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
   * 
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
   * Put a message back onto the front of the queue the queue for the user with 
   * the given username. Keep trying until it works.
   * @param username
   * @param message
   */
  public static void putMessageBackToDeliverLater(String username, PendingMessage message) {
    while(true) {
      try {
        pendingMessages.get(username).putFirst(message);
        return;
      } catch (InterruptedException e) {}
    }
  }

  /**
   * Return whether a user exists
   * @param username
   */
  public static boolean doesUserExists(String username) {
    return messageDistributors.containsKey(username);
  }

  /**
   * Return whether a user is logged in
   * @param username
   */
  public static boolean isLoggedIn(String username) {
    return 
    doesUserExists(username) &&
      !messageDistributors.get(username).equals(EMPTY_MESSAGE_DISTRIBUTOR);
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

        /**
         * The user that this StreamObserver is responsible for
         * Becomes populated when the user logs in
         * And becomes null when the user logs out
         * Is used to determine the sender when sending messages
         * and to determine who to log out when a log out request is received
         * (hint: its this.username!)
         */
        String username = null;

        /**
         * Make the responseObserver thread-safe
         */
        ConcurrentStreamObserver<ChatMessage> cResponseObserver = new ConcurrentStreamObserver<ChatMessage>(responseObserver);

        /**
         * Logs out the user that this ResponseObserver is responsible for
         * Does not check for invariants that are required for logging out. 
         * Simply performs the actions required to log a user out.
         * Thus, callers should check invariants before calling this method 
         * such as whether the user is even logged in.
         */
        private void logOut() {
          // store the username in a local variable so we can use it after we null out this.username
          String username = this.username;

          // stop the MessageDistributor and remove it for the user if it exists
          if (doesUserExists(username)
            && messageDistributors.get(username).isPresent()) {
            messageDistributors.get(username).get().cease();
            messageDistributors.put(username, EMPTY_MESSAGE_DISTRIBUTOR);
          }

          // and this ResponseObserver no longer represents them
          this.username = null;

          // respond with a success message
          cResponseObserver.onNext(
              ChatMessageGenerator.LOG_OUT_SUCCESS(username));
          
          logger.info("Logged out " + username);
        }

        @Override
        public void onNext(ChatMessage message) {
          // If this ResponseObserver is currently responsible for a user, check if the user still exists (in case they got deleted). If deleted, then log them out
          if (this.username != null && !messageDistributors.containsKey(username)) {
            logger.info("User " + username + " was deleted. Logging them out.");
            logOut();
            return;
          }

          // handle the message based on what type ("case") it is
          switch (message.getMessageCase()) {
            case CREATE_ACCOUNT_REQUEST: {
              String username = message.getCreateAccountRequest().getUsername();
              // respond with an exception if the username is already taken
              if (messageDistributors.containsKey(username)) {
                logger.info(
                    "Failed to create account for " + username + " because the username is already taken");
                cResponseObserver.onNext(
                    ChatMessageGenerator
                        .CREATE_ACCOUNT_USER_ALREADY_EXISTS(username));
                return;
              }
              // mark the user as created but not logged in yet
              messageDistributors.put(username, EMPTY_MESSAGE_DISTRIBUTOR);
              // create a new queue for the user to hold pending messages
              pendingMessages.put(
                  username,
                  new LinkedBlockingDeque<PendingMessage>());
              logger.info("Created account for " + username);
              // respond with a success message
              cResponseObserver.onNext(
                  ChatMessageGenerator.CREATE_ACCOUNT_SUCCESS(username));
              break;
            }
            case LOG_IN_REQUEST: {
              String username = message.getLogInRequest().getUsername();
              // respond with an exception if the username does not exist
              if (!messageDistributors.containsKey(username)) {
                logger.info(
                    "Failed to log in " + username + " because the username does not exist");
                cResponseObserver.onNext(
                    ChatMessageGenerator
                        .LOG_IN_USER_DOES_NOT_EXIST(username));
                return;
              }

              // create a new thread to distribute messages to the user on demand
              MessageDistributor md = new MessageDistributor(username, cResponseObserver);
              messageDistributors.put(username, Optional.of(md));
              md.start();

              // if this ResponseObserver was previously representing another user that still exists, log that user out first
              if (this.username != null) {
                logOut();
              }

              // mark this ResponseObserver as representing the user
              this.username = username;

              logger.info("Logged in " + username);
              // respond with a success message
              cResponseObserver.onNext(
                  ChatMessageGenerator.LOG_IN_SUCCESS(username));
              break;
            }
            case LOG_OUT_REQUEST: {
              // respond with an exception if the client represented by this ResponseObserver is not logged in or if the account no longer exists
              if (this.username == null || !messageDistributors.containsKey(this.username)) {
                logger.info("Failed to log out because the user is not logged in");
                cResponseObserver.onNext(
                    ChatMessageGenerator.LOG_OUT_USER_NOT_LOGGED_IN(
                        this.username));
                return;
              } else {
                logOut();
              }
              break;
            }
            case SEND_MESSAGE_REQUEST: {
              String recipient = message.getSendMessageRequest().getRecipient();
              String messageText = message.getSendMessageRequest().getMessage();

              // respond with an exception if the client represented by this ResponseObserver is not logged in
              if (this.username == null) {
                logger.info("Failed to send message because the user is not logged in");
                cResponseObserver.onNext(
                    ChatMessageGenerator.SEND_MESSAGE_USER_NOT_LOGGED_IN(
                        this.username));
                return;
              }

              // respond with an exception if the recipient does not exist
              // note: despite the fact that the variable is called logInStatus, here we are checking if the recipient username EXISTS in the table; NOT whether they're logged in
              if (!messageDistributors.containsKey(recipient)) {
                logger.info("Failed to send message because the recipient does not exist");
                cResponseObserver.onNext(
                    ChatMessageGenerator.SEND_MESSAGE_RECIPIENT_DOES_NOT_EXIST(recipient));
                return;
              }

              // put the message onto the end of the recipient's queue of pending messages (keep trying until it works)
              while(true) {
                try {
                  pendingMessages.get(recipient).put(
                      new PendingMessage(recipient, this.username, messageText));
                  break;
                } catch (InterruptedException e) {}
              }

              logger.info("Queued message from " + this.username + " to " + recipient);

              // respond with a success message
              cResponseObserver.onNext(
                  ChatMessageGenerator.SEND_MESSAGE_SUCCESS(this.username, recipient));

              break;
            }
            case LIST_ACCOUNTS_REQUEST: {
              String regexString = message.getListAccountsRequest().getPattern();

              // Make * match any number of characters
              regexString = regexString.replaceAll("\\*", ".*");

              // Ensure the regex is anchored
              regexString = '^' + regexString + '$';

              Pattern regex = Pattern.compile(regexString);

              // Compare the query to the list of users
              ArrayList<String> matchedUsers = new ArrayList<String>();
              for(String e : messageDistributors.keySet()) {
                  Matcher matcher = regex.matcher(e);

                  // If the regex matches the username, add the username
                  if (matcher.find()) {
                      matchedUsers.add(e);
                  }
              }

              // Send a message with the list of users
              cResponseObserver.onNext(
                  ChatMessageGenerator.LIST_ACCOUNTS(matchedUsers));
              break;
            }
            case DELETE_ACCOUNT_REQUEST: {
              String username = message.getDeleteAccountRequest().getUsername();

              // respond with an exception if the account does not exist
              if (!messageDistributors.containsKey(username)) {
                logger.info(
                    "Failed to delete account " + username + " because the username does not exist");
                cResponseObserver.onNext(
                    ChatMessageGenerator
                        .DELETE_ACCOUNT_USER_DOES_NOT_EXIST(username));
                return;
              }

              // delete the account by
              // (1) stopping the MessageDistributor for the user if it exists
              if (messageDistributors.get(username).isPresent()) {
                messageDistributors.get(username).get().cease();
              }
              // (2) deleting the user's entry in the messageDistributors 
              //     map, marking them as deleted
              messageDistributors.remove(username);
              // (3) deleting the user's pending messages
              pendingMessages.remove(username);

              // respond with a success message
              cResponseObserver.onNext(
                  ChatMessageGenerator.DELETE_ACCOUNT_SUCCESS(username));
              break;
            }
            default:
              // Ignore unknown message types
              break;
          }
        }

        @Override
        public void onError(Throwable t) {
          // if the client disconnects, log them out
          if (this.username != null) {
            // stop the MessageDistributor for the user
            messageDistributors.get(this.username).get().cease();
            // then remove the MessageDistributor, marking the user as logged out
            messageDistributors.put(username, EMPTY_MESSAGE_DISTRIBUTOR);
            logger.info("Logged out " + this.username + " because they disconnected");
          }
        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();
        }
      };
    }
  }
}