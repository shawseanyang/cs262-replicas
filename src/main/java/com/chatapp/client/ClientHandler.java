package com.chatapp.client;

import com.chatapp.Chat;
import com.chatapp.ChatServiceGrpc;
import com.chatapp.Chat.CreateAccountRequest;
import com.chatapp.ChatServiceGrpc.ChatServiceStub;
import com.chatapp.ChatServiceGrpc.ChatServiceBlockingStub;
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

import com.chatapp.protocol.Exception;

// Helps the Client handle commands from the user because the Client is responsible for the UI logic. Each of the functions handles its command bu sending requests to the server, waiting for a response, then passing the response back to the caller.

// define new exceptions below UserAlreadyExists and UserDoesNotExist


public class ClientHandler {
  public static void createAccount(CreateAccountCommand command, ChatServiceBlockingStub stub) throws UserAlreadyExistsException, InvalidUsernameException {
    // create request using command
    CreateAccountRequest request =
      Chat.CreateAccountRequest.newBuilder()
        .setUsername(command.getUsername())
        .build();
    // Finally, make the call using the stub
    Chat.CreateAccountResponse response = 
      stub.createAccount(request);
    // handle exceptions
    if (response.getException() != 0) {
      switch (com.chatapp.protocol.Exception.fromInt(response.getException())) {
        case USER_ALREADY_EXISTS:
          throw new UserAlreadyExistsException("Cannot create account because the username already exists.");
        case INVALID_USERNAME:
          throw new InvalidUsernameException("Cannot create account because the username is invalid.");
        default:
          break;
      }
    }
  }

  public static void deleteAccount(DeleteAccountCommand command, ChatServiceBlockingStub stub) throws InvalidUsernameException {

  }

  public static void listAccounts(ListAccountsCommand command, ChatServiceBlockingStub stub) {

  }

  // returns the sessionId given by the server in a String
  public static String logIn(LogInCommand command, ChatServiceBlockingStub stub) throws UserDoesNotExistException {
    return "";
  }

  public static void logOut(LogOutCommand command, ChatServiceBlockingStub stub, String sessionId) throws NotLoggedInException {

  }

  public static void sendMessage(SendMessageCommand command, ChatServiceBlockingStub stub, String sessionId) throws UserDoesNotExistException, NotLoggedInException
  {

  }
}
