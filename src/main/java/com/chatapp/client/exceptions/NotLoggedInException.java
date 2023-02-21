package com.chatapp.client.exceptions;

// An exception that is thrown when the user is not logged in

public class NotLoggedInException extends Exception {
  public NotLoggedInException(String message) {
    super(message);
  }
}
