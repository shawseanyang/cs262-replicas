package com.chatapp.server;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountResponse;
import com.chatapp.Chat.DistributeMessageRequest;
import com.chatapp.Chat.LogInResponse;
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
}
