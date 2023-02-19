package com.chatapp.client;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountRequest;
import com.chatapp.Chat.CreateAccountResponse;
import com.chatapp.Chat.DeleteAccountRequest;
import com.chatapp.Chat.ListAccountsRequest;
import com.chatapp.Chat.LogInRequest;
import com.chatapp.Chat.LogOutRequest;
import com.chatapp.Chat.SendMessageRequest;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;

import com.google.rpc.Status;
import com.google.rpc.Code;
import io.grpc.stub.StreamObserver;

// Connects to the server in a separate thread, and handles all communication with the server.

public class ConnectionManager extends Thread {

  ChatServiceStub stub;

  public ConnectionManager(ChatServiceStub stub) {
    this.stub = stub;
  }
  
  /**
   * Connects to the server, defining the callback handlers in the process, then enters an infinite loop where it pops commands off the queue and executes them. It blocks if the command queue is empty.
   */
  public void run() {
    System.out.println("-> Connecting to server.");

    // define the callback handlers
    StreamObserver<ChatMessage> requestObserver =
        stub.chat(new StreamObserver<ChatMessage>() {
          @Override
          public void onNext(ChatMessage message) {
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
  
          @Override
          public void onError(Throwable t) {
            System.err.println("-> Fatal error, disconnected from the server: " + t);
            System.exit(1);
          }
  
          @Override
          public void onCompleted() {
            System.out.println("-> Disconnected from server.");
          }
        });
    
    System.out.println("-> Connected to server.");
    
    // enter an infinite loop where we pop commands off the queue and execute them
    while(true) {
      Command command;
      try {
        command = Client.getNextCommand();
      } catch (InterruptedException e) {
        continue;
      }
      
      try {
        if (command instanceof CreateAccountCommand) {
          CreateAccountCommand cast = (CreateAccountCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setCreateAccountRequest(
              CreateAccountRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
          requestObserver.onNext(message);
        }
        else if (command instanceof DeleteAccountCommand) {
          DeleteAccountCommand cast = (DeleteAccountCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setDeleteAccountRequest(
              DeleteAccountRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
          requestObserver.onNext(message);
        }
        else if (command instanceof ListAccountsCommand) {
          ListAccountsCommand cast = (ListAccountsCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setListAccountsRequest(
              ListAccountsRequest.newBuilder()
                .setPattern(cast.getPattern())
                .build()
            ).build();
          requestObserver.onNext(message);
        }
        else if (command instanceof LogInCommand) {
          LogInCommand cast = (LogInCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setLogInRequest(
              LogInRequest.newBuilder()
                .setUsername(cast.getUsername())
                .build()
            ).build();
          requestObserver.onNext(message);
        }
        else if (command instanceof LogOutCommand) {
          // create a LogOutResponse, which contains nothing
          ChatMessage message = ChatMessage.newBuilder().setLogOutRequest(LogOutRequest.newBuilder().build()).build();
          requestObserver.onNext(message);
        }
        else if (command instanceof SendMessageCommand) {
          SendMessageCommand cast = (SendMessageCommand) command;
          ChatMessage message = ChatMessage.newBuilder()
            .setSendMessageRequest(
              SendMessageRequest.newBuilder()
                .setRecipient(cast.getRecipient())
                .setMessage(cast.getMessage())
                .build()
            ).build();
          requestObserver.onNext(message);
        }
      } catch (Exception e) {
        System.err.println("-> Error: " + e.getMessage());
      }
    }
  }
  
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
