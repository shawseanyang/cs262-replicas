package com.chatapp.server;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.CreateAccountResponse;
import com.google.rpc.Code;
import com.google.rpc.Status;

public class ChatMessageGenerator {
  public static ChatMessage CREATE_ACCOUNT_SUCCESS() {
    return ChatMessage.newBuilder()
        .setCreateAccountResponse(
            CreateAccountResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Account created successfully")
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
}
