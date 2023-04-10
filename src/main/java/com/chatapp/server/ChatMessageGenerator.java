package com.chatapp.server;

import java.util.ArrayList;

import com.chatapp.Chat.ChatMessage;
import com.chatapp.Chat.Content;
import com.chatapp.Chat.CreateAccountResponse;
import com.chatapp.Chat.DeleteAccountResponse;
import com.chatapp.Chat.DistributeMessageRequest;
import com.chatapp.Chat.ListAccountsResponse;
import com.chatapp.Chat.LogInResponse;
import com.chatapp.Chat.LogOutResponse;
import com.chatapp.Chat.Pong;
import com.chatapp.Chat.RejectedByFollower;
import com.chatapp.Chat.SendMessageResponse;
import com.google.rpc.Code;
import com.google.rpc.Status;

/**
 * A class that contains static methods that generate ChatMessages for various 
 * server responses.
 */

public class ChatMessageGenerator {
  public static ChatMessage CREATE_ACCOUNT_SUCCESS(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setCreateAccountResponse(
            CreateAccountResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Account created for " + username)
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage CREATE_ACCOUNT_USER_ALREADY_EXISTS(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setCreateAccountResponse(
            CreateAccountResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.ALREADY_EXISTS.getNumber())
                        .setMessage("The username " + username + " is already taken")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage LOG_IN_SUCCESS(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setLogInResponse(
            LogInResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Logged in as " + username)
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage LOG_IN_USER_DOES_NOT_EXIST(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setLogInResponse(
            LogInResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage("Cannot log in because the user " + username + " does not exist")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage DISTRIBUTE_MESSAGE(String sender, String message) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setDistributeMessageRequest(
            DistributeMessageRequest.newBuilder()
                .setSender(sender)
                .setMessage(message)
                .build())
          .build())
        .build();
  }

  public static ChatMessage LOG_OUT_SUCCESS(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setLogOutResponse(
            LogOutResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Logged out " + username)
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage LOG_OUT_USER_NOT_LOGGED_IN(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setLogOutResponse(
            LogOutResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.FAILED_PRECONDITION.getNumber())
                        .setMessage("Cannot log out because you are not logged in")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_SUCCESS(String sender, String recipient) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.OK.getNumber())
                        .setMessage("Queued message from " + sender + " for " + recipient + "!")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_RECIPIENT_DOES_NOT_EXIST(String recipient) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage("Cannot send message to " + recipient + " because that user does not exist")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage SEND_MESSAGE_USER_NOT_LOGGED_IN(String recipient) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setSendMessageResponse(
            SendMessageResponse.newBuilder()
                .setStatus(
                    Status.newBuilder()
                        .setCode(Code.FAILED_PRECONDITION.getNumber())
                        .setMessage("Cannot send a message because you're not logged in")
                        .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage LIST_ACCOUNTS(ArrayList<String> accounts) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setListAccountsResponse(
            ListAccountsResponse.newBuilder()
                .addAllAccounts(accounts)
                .build())
          .build())
        .build();
  }

  public static ChatMessage DELETE_ACCOUNT_USER_DOES_NOT_EXIST(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setDeleteAccountResponse(
            DeleteAccountResponse.newBuilder()
                .setStatus(Status.newBuilder()
                    .setCode(Code.NOT_FOUND.getNumber())
                    .setMessage("Cannot delete account because the user " + username + " does not exist")
                    .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage DELETE_ACCOUNT_SUCCESS(String username) {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setDeleteAccountResponse(
            DeleteAccountResponse.newBuilder()
                .setStatus(Status.newBuilder()
                    .setCode(Code.OK.getNumber())
                    .setMessage("Deleted account named " + username)
                    .build())
                .build())
          .build())
        .build();
  }

  public static ChatMessage REJECTED() {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setRejectedByFollower(
            RejectedByFollower.newBuilder().build())
          .build())
        .build();
  }

  public static ChatMessage PONG() {
    return ChatMessage.newBuilder()
      .setContent(Content.newBuilder()
        .setPong(
            Pong.newBuilder().build())
          .build())
        .build();
  }
}