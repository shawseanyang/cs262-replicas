package com.chatapp.client;

import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogInCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;
import com.chatapp.client.exceptions.InvalidUsernameException;
import com.chatapp.client.exceptions.NotLoggedInException;
import com.chatapp.client.exceptions.UserAlreadyExistsException;
import com.chatapp.client.exceptions.UserDoesNotExistException;

import io.grpc.ManagedChannel;

// Helps the Client handle commands from the user because the Client is responsible for the UI logic. Each of the functions handles its command bu sending requests to the server, waiting for a response, then passing the response back to the caller.

// define new exceptions below UserAlreadyExists and UserDoesNotExist


public class ClientHandler {
  public static void createAccount(CreateAccountCommand command, ManagedChannel channel) throws UserAlreadyExistsException, InvalidUsernameException {

  }

  public static void deleteAccount(DeleteAccountCommand command, ManagedChannel channel) throws InvalidUsernameException {

  }

  public static void listAccounts(ListAccountsCommand command, ManagedChannel channel) {

  }

  // returns the sessionId given by the server in a String
  public static String logIn(LogInCommand command, ManagedChannel channel) throws UserDoesNotExistException {
    return "";
  }

  public static void logOut(LogOutCommand command, ManagedChannel channel, String sessionId) throws NotLoggedInException {

  }

  public static void sendMessage(SendMessageCommand command, ManagedChannel channel, String sessionId) throws UserDoesNotExistException, NotLoggedInException
  {

  }
}
