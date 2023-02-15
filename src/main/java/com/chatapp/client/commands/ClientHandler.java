package com.chatapp.client.commands;

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

  public static void logIn(LogInCommand command, ManagedChannel channel) throws UserDoesNotExistException {

  }

  public static void logOut(LogOutCommand command, ManagedChannel channel) throws NotLoggedInException {

  }

  public static void sendMessage(SendMessageCommand command, ManagedChannel channel) throws UserDoesNotExistException, NotLoggedInException
  {

  }
}
