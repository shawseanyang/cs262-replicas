package com.chatapp.client;

import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountRequest;
import com.chatapp.Chat.DeleteAccountRequest;
import com.chatapp.Chat.ListAccountsRequest;
import com.chatapp.Chat.LogInRequest;
import com.chatapp.Chat.LogOutRequest;
import com.chatapp.Chat.PingRequest;
import com.chatapp.Chat.SendMessageRequest;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;
import com.chatapp.protocol.Server;
import com.google.rpc.Status;
import com.google.rpc.Code;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Grabs commands from the command queue of the Client and executes them by sending requests to the server. This runs in a new Thread to keep the UI nimble and none-blocking. It also defines the callback handlers for what to do when the server sends a message back to the client. This mainly involves printing it out for the user to see.
*/

public class ConnectionManager extends Thread {
  
  // The server manager is used to get the current server and to get the next server in the list
  ServerManager serverManager;

  // The observer is used to send messages to the server
  StreamObserver<ChatMessage> observer;

  // The channel represents the connection to the server and is used to close the connection when the server is changed
  ManagedChannel channel;

  // Constructor
  public ConnectionManager() {
    serverManager = new ServerManager();
    observer = createObserverFor(serverManager.getCurrent());
    // try sending a ping to the server to see if it is alive
    observer.onNext(ChatMessage.newBuilder().setPingRequest(PingRequest.newBuilder().build()).build());
  }

  private StreamObserver<ChatMessage> createObserverFor(Server server) {
    // close the previous channel if it exists
    if (channel != null) {
      channel.shutdown();
    }
    channel = ManagedChannelBuilder.forTarget(server.toString())
      .usePlaintext()
      .build();
    ChatServiceStub stub = ChatServiceGrpc.newStub(channel);
          
    return
      // the callback methods for the "chat" rpc, which is the bidirectional streaming rpc that is used to communicate with the server
      stub.chat(new StreamObserver<ChatMessage>() {

        /**
         * Try connecting to the next available server
         */
        public void reconnectToNextServer() {
          System.out.println("-> Disconnected from server " + serverManager.getCurrent().toString() + ". Trying next server.");

          observer = createObserverFor(serverManager.getNext());

          System.out.println("-> Reconnected to server " + serverManager.getCurrent().toString() + ".");
        }

        // this callback is called when the server sends a message to the client
        @Override
        public void onNext(ChatMessage message) {
          // if the request was rejected because the server was a follower, then print a message to the user
          if (message.hasRejectedByFollower()) {
            System.out.println("-> The server is a follower, reconnecting to another server.");
            reconnectToNextServer();
            return;
          }

          // if the message is a message distribution, print the message for the user to see
          if (message.hasDistributeMessageRequest()) {
            // use printf
            System.out.printf(
              "[%s]: %s\n",
              message.getDistributeMessageRequest().getSender(),
              message.getDistributeMessageRequest().getMessage()
            );
            return;
          }

          // if the message is a listing of accounts, then print the accounts for the user to see. Use a StringBuilder so that it can be printed all at once to avoid being interrupted by some other print statement
          if (message.hasListAccountsResponse()) {
            StringBuilder sb = new StringBuilder();
            sb.append("*** Accounts ***\n");
            for (String account : message.getListAccountsResponse().getAccountsList()) {
              sb.append(account + "\n");
            }
            sb.append("****************");
            System.out.println(sb.toString());
            return;
          }

          // All other messages are "confirmations", where the server is confirming the success of a client's request, simply print the message from the server, adding an "Error:" prefix if the server returned an error code 
          Status status = extractStatus(message);
          if (status == null) {
            System.out.println("-> Error parsing server response");
            return;
          } else if (status.getCode() != Code.OK_VALUE) {
            System.out.println("-> Error: " + status.getMessage());
            return;
          } else {
            System.out.println("-> " + status.getMessage());
          }
        }
        
        // If the server returns an error, it means the connection has been severed, so try connecting to the next server in the list
        @Override
        public void onError(Throwable t) {
          reconnectToNextServer();
        }
        
        /**
         * This callback is called when the gRPC connection closes gracefully.
         */
        @Override
        public void onCompleted() {
          System.out.println("-> Disconnected from server.");
        }
      });
    }
  
  /**
   * This method is run when the thread is started.
   * Connects to the server, defining the callback handlers in the process, then enters an infinite loop where it pops commands off the queue and executes them. It blocks if the command queue is empty.
   */
  public void run() {
    // enter an infinite loop where we pop commands off the queue and execute them
    while(true) {
      // pop a command off the queue
      Command command;
      try {
        command = Client.getNextCommand();
      } catch (InterruptedException e) {
        continue;
      }
      
      // Try to execute the command by casting it, creating a message, and sending the message to the server
      try {
        // ------------------ CREATE ACCOUNT ------------------
        if (command instanceof CreateAccountCommand) {
          CreateAccountCommand cast = (CreateAccountCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setCreateAccountRequest(
              CreateAccountRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
            observer.onNext(message);
        }

        // ------------------ DELETE ACCOUNT ------------------
        else if (command instanceof DeleteAccountCommand) {
          DeleteAccountCommand cast = (DeleteAccountCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setDeleteAccountRequest(
              DeleteAccountRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
            observer.onNext(message);
        }

        // ------------------ LIST ACCOUNTS ------------------
        else if (command instanceof ListAccountsCommand) {
          ListAccountsCommand cast = (ListAccountsCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setListAccountsRequest(
              ListAccountsRequest.newBuilder()
                .setPattern(cast.getPattern())
                .build()
            ).build();
            observer.onNext(message);
        }

        // ------------------ LOG IN ------------------
        else if (command instanceof LogInCommand) {
          LogInCommand cast = (LogInCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setLogInRequest(
              LogInRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
            observer.onNext(message);
        }

        // ------------------ LOG OUT ------------------
        else if (command instanceof LogOutCommand) {
          // create a LogOutResponse, which contains nothing
          ChatMessage message = ChatMessage.newBuilder().setLogOutRequest(LogOutRequest.newBuilder().build()).build();
          observer.onNext(message);
        }

        // ------------------ SEND MESSAGE ------------------
        else if (command instanceof SendMessageCommand) {
          SendMessageCommand cast = (SendMessageCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setSendMessageRequest(
              SendMessageRequest.newBuilder()
                .setRecipient(cast.getRecipient())
                .setMessage(cast.getMessage())
                .build()
            ).build();
            observer.onNext(message);
        }
      } catch (Exception e) {
        // if an exception is thrown, print the error message
        System.err.println("-> Error: " + e.getMessage());
      }
    }
  }
  
  /**
   * Extracts the status from a ChatMessage. 
   * @param message
   * @return
   */
  private Status extractStatus(ChatMessage message) {
    Status status;
    // go through all the message types, setting status to the status of the message if its not null -- since ChatMessage is a oneof, only one of these will be non-null and thus set
    if (message.hasCreateAccountResponse()) {
      status = message.getCreateAccountResponse().getStatus();
    } else if (message.hasLogInResponse()) {
      status = message.getLogInResponse().getStatus();
    } else if (message.hasLogOutResponse()) {
      status = message.getLogOutResponse().getStatus();
    } else if (message.hasSendMessageResponse()) {
      status = message.getSendMessageResponse().getStatus();
    } else if (message.hasDeleteAccountResponse()) {
      status = message.getDeleteAccountResponse().getStatus();
    } else {
      status = null;
    }
    return status;
  }
}
