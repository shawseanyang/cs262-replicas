package com.chatapp.client;

import java.util.HashSet;

import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.EmptyCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.QuitCommand;
import com.chatapp.client.commands.SendMessageCommand;

/*
 * Parses a single string representing a commandline command into a Command object
 * syntax: <command> <arg1> <arg2> ...
 */
public class CommandParser {

  static final HashSet<String> quitCommands = new HashSet<String>() {{
    add("e");
    add("exit");
    add("q");
    add("quit");
  }};

  public static Command parse(String command) throws IllegalArgumentException {
    if (command.isEmpty()) {
      return new EmptyCommand();
    }
    try {
      String[] parts = command.split(" ");
      String commandName = parts[0];
      String[] args = new String[parts.length - 1];
      System.arraycopy(parts, 1, args, 0, args.length);
      if (quitCommands.contains(commandName)) {
        return new QuitCommand();
      }
      switch (commandName) {
        case "connect":
          return new ConnectCommand(args[0]);
        case "create_account":
          return new CreateAccountCommand(args[0]);
        case "list_accounts":
          return new ListAccountsCommand(args[0]);
        case "logout":
          return new LogOutCommand();
        case "login":
          return new LogInCommand(args[0]);
        case "delete_account":
          return new DeleteAccountCommand(args[0]);
        case "send":
          // Recombine the message
          StringBuilder message = new StringBuilder();
          for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
            if (i != args.length - 1) {
              message.append(" ");
            }
          }
          return new SendMessageCommand(args[0], message.toString());
        default:
          throw new IllegalArgumentException("Unknown command: " + commandName);
      }
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        throw e;
      } else {
        throw new IllegalArgumentException("Invalid command: " + command);
      }
    }
  }
}