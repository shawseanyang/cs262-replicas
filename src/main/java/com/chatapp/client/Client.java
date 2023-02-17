package com.chatapp.client;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.ChatServiceGrpc;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;
import com.chatapp.protocol.Constant;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

// Entry point of the client application. Listens for user commands from the console and executes them.

public class Client {
  static Scanner in = new Scanner(System.in);
  static ChatServiceStub stub;
  static BlockingQueue<Command> pendingCommands = new LinkedBlockingQueue<Command>();

  /**
   * Entry point. Parses user commands from the console and adds then to the queue of pending commands unless the command is connect, in which case it creates a new channel, creates a stub, and connects to the server.
   */
  public static void main(String[] args) {
    // listen for new user commands from the console
    while(true) {
      // read the next command from the console
      Command command;
      try {
        command = CommandParser.parse(in.nextLine());
      } catch (IllegalArgumentException e) {
        System.out.println("-> Error: " + e.getMessage());
        continue;
      }

      // when its a connect command, create a new channel, create a stub, then connect to the server using the ConnectionManager, which splits off into a separate thread
      if (command instanceof ConnectCommand) {
        ConnectCommand cast = (ConnectCommand) command;
        ManagedChannel channel =
          ManagedChannelBuilder.forAddress(cast.getHost(), Constant.PORT)
          .usePlaintext()
          .build();
        stub = ChatServiceGrpc.newStub(channel);
        try {
          ConnectionManager cm = new ConnectionManager(stub);
          cm.start(); // new thread
        } catch (Exception e) {
          System.out.println("-> Failed to connect: " + e.getMessage());
        }
        continue;
      }
      
      // if there is no stub, then the user must connect first
      if (stub == null) {
        System.out.println("-> Error: You must connect to a server first.");
        continue;
      }

      // add the command to the queue of pending commands
      try {
        pendingCommands.put(command);
      } catch (InterruptedException e) {
        System.out.println("-> Error: " + e.getMessage());
      }
    }
  }

  /*
   * Allows the ConnectionManager to get the next command from the queue.
   */
  public static Command getNextCommand() throws InterruptedException {
    return pendingCommands.take();
  }
}
