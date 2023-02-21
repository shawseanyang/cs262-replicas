package com.chatapp.client.exceptions;

// An exception that is thrown when the user already exists

public class UserAlreadyExistsException extends Exception {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
