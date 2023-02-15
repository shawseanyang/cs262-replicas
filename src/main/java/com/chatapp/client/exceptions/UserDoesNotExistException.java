package com.chatapp.client.exceptions;

public class UserDoesNotExistException extends Exception {
  public UserDoesNotExistException(String message) {
    super(message);
  }
}
