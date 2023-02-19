package com.chatapp.server;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountResponse;
import com.chatapp.Chat.DistributeMessageRequest;
import com.chatapp.Chat.LogInResponse;
import com.chatapp.Chat.LogOutResponse;
import com.chatapp.Chat.SendMessageResponse;
import com.google.rpc.Code;
import com.google.rpc.Status;

public class ChatMessageGenerator {
  public static ChatMessage CREATE_ACCOUNT_SUCCESS(String username) {
    return ChatMessage.newBuilder()
        .setCreateAccountResponse(
            CreateAccountResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Account created for " + username)
                        .build())
                .build())
        .build();
  }

  public static ChatMessage CREATE_ACCOUNT_USER_ALREADY_EXISTS(String username) {
    return ChatMessage.newBuilder()
        .setCreateAccountResponse(
            CreateAccountResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.ALREADY_EXISTS.getNumber())
                        .setMessage("The username " + username + " is already taken")
                        .build())
                .build())
        .build();
  }

  public static ChatMessage LOG_IN_SUCCESS(String username) {
    return ChatMessage.newBuilder()
        .setLogInResponse(
            LogInResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Logged in as " + username)
                        .build())
                .build())
        .build();
  }

  public static ChatMessage LOG_IN_USER_DOES_NOT_EXIST(String username) {
    return ChatMessage.newBuilder()
        .setLogInResponse(
            LogInResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage("Cannot log in because the user " + username + " does not exist")
                        .build())
                .build())
        .build();
  }

  public static ChatMessage DISTRIBUTE_MESSAGE(String sender, String message) {
    return ChatMessage.newBuilder()
        .setDistributeMessageRequest(
            DistributeMessageRequest.newBuilder()
                .setSender(sender)
                .setMessage(message)
                .build())
        .build();
  }

  public static ChatMessage LOG_OUT_SUCCESS(String username) {
    return ChatMessage.newBuilder()
        .setLogOutResponse(
            LogOutResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Logged out " + username)
                        .build())
                .build())
        .build();
  }

  public static ChatMessage LOG_OUT_USER_NOT_LOGGED_IN(String username) {
    return ChatMessage.newBuilder()
        .setLogOutResponse(
            LogOutResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.FAILED_PRECONDITION.getNumber())
                        .setMessage("Cannot log out because you are not logged in")
                        .build())
                .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_SUCCESS(String sender, String recipient) {
    return ChatMessage.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Queued message from " + sender + " for " + recipient + "!")
                        .build())
                .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_RECIPIENT_DOES_NOT_EXIST(String recipient) {
    return ChatMessage.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage("Cannot send message to " + recipient + " because that user does not exist")
                        .build())
                .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_USER_NOT_LOGGED_IN(String recipient) {
    return ChatMessage.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.FAILED_PRECONDITION.getNumber())
                        .setMessage("Cannot send a message because you're not logged in")
                        .build())
                .build())
        .build();
  }
}