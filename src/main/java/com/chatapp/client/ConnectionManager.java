package com.chatapp.client;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountRequest;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;

import io.grpc.Status;
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
            // TODO
            System.out.println(message);
          }
  
          @Override
          public void onError(Throwable t) {
            Status status = Status.fromThrowable(t);
            System.err.println("-> Error: " + status);
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
        }
        else if (command instanceof ListAccountsCommand) {
          ListAccountsCommand cast = (ListAccountsCommand) command;
        }
        else if (command instanceof LogInCommand) {
          LogInCommand cast = (LogInCommand) command;
        }
        else if (command instanceof LogOutCommand) {
          LogOutCommand cast = (LogOutCommand) command;
        }
        else if (command instanceof SendMessageCommand) {
          SendMessageCommand cast = (SendMessageCommand) command;
        }
      } catch (Exception e) {
        System.err.println("-> Error: " + e.getMessage());
      }
    }
  }
}
