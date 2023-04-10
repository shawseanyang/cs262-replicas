package com.chatapp.client;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.EmptyCommand;
import com.chatapp.client.commands.QuitCommand;

// Entry point of the client application. Listens for user commands from the console and adds it to its queue of pending commands. It exposes a method for the ConnectionManager to get the next command from the queue.

public class Client {
  // Scanner for reading user input from the console
  static Scanner in = new Scanner(System.in);

  // A queue of pending commands that are waiting to be executed
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

      // Check for exit command
      if (command instanceof QuitCommand) {
        return;
      }

      // Skip empty commands
      if (command instanceof EmptyCommand) {
        continue;
      }

      // when its a connect command, create a connection manager, which tries to connect to the server
      if (command instanceof ConnectCommand) {
        (new ConnectionManager()).start();
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
