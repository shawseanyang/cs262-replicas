package com.chatapp.client;

import java.util.Scanner;

import com.chatapp.ChatServiceGrpc;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// Entry point of the client application. Listens for user commands from the console and executes them. This class is responsible for UI logic. It is responsible for creating a channel to the server, and then passing the channel to the handlers.

public class Client {
  static Scanner in = new Scanner(System.in);
  static ManagedChannel channel;
  static String sessionId;

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

      // when its a connect command, create a new channel to the server
      if (command instanceof ConnectCommand) {
        ConnectCommand cast = (ConnectCommand) command;
        channel = ManagedChannelBuilder
          .forTarget(cast.getHost() + ":" + cast.getPort())
          .usePlaintext(true)
          .build();
        continue;
      }
      
      // if there is no channel, then the user must connect first
      if (channel == null) {
        System.out.println("-> Error: You must connect to a server first.");
        continue;
      }

      // execute the command
      if (command instanceof CreateAccountCommand) {
        CreateAccountCommand cast = (CreateAccountCommand) command;
        try {
          //createAccount(cast, channel);
        } catch (Exception e) {
          System.out.println("-> Error: " + e.getMessage());
        }
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
    }
  }
}
