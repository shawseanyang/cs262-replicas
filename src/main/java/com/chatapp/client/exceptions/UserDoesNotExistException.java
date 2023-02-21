package com.chatapp.client.exceptions;

// An exception that is thrown when the user does not exist

public class UserDoesNotExistException extends Exception {
  public UserDoesNotExistException(String message) {
    super(message);
  }
}
