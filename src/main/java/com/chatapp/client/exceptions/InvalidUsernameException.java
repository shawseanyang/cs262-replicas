package com.chatapp.client.exceptions;

// An exception that is thrown when the username is invalid

public class InvalidUsernameException extends Exception {
  public InvalidUsernameException(String message) {
    super(message);
  }
}
