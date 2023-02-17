package com.chatapp.client;

import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;

// Parses a single string representing a commandline command into a Command object

// syntax: <command> <arg1> <arg2> ...

public class CommandParser {
  public static Command parse(String command) throws IllegalArgumentException {
    try {
      String[] parts = command.split(" ");
      String commandName = parts[0];
      String[] args = new String[parts.length - 1];
      System.arraycopy(parts, 1, args, 0, args.length);
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
          return new SendMessageCommand(args[0], args[1]);
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
