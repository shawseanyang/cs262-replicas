package com.chatapp.server;

import java.util.ArrayList;

import org.junit.Test;

import com.chatapp.Chat.ChatMessage;
import com.google.rpc.Code;

public class ChatMessageGeneratorTest {
  @Test
  public void CREATE_ACCOUNT_SUCCESS() {
    ChatMessage message = ChatMessageGenerator.CREATE_ACCOUNT_SUCCESS("username");

    assert(message.getContent().getCreateAccountResponse().getStatus().getCode() == Code.OK.getNumber());
    
    assert(message.getContent().getCreateAccountResponse().getStatus().getMessage().equals("Account created for username"));
  }

  @Test
  public void CREATE_ACCOUNT_USER_ALREADY_EXISTS() {
    ChatMessage message = ChatMessageGenerator.CREATE_ACCOUNT_USER_ALREADY_EXISTS("username");

    assert(message.getContent().getCreateAccountResponse().getStatus().getCode() == Code.ALREADY_EXISTS.getNumber());
    
    assert(message.getContent().getCreateAccountResponse().getStatus().getMessage().equals("The username username is already taken"));
  }

  @Test
  public void LOG_IN_SUCCESS() {
    ChatMessage message = ChatMessageGenerator.LOG_IN_SUCCESS("username");

    assert(message.getContent().getLogInResponse().getStatus().getCode() == Code.OK.getNumber());
    
    assert(message.getContent().getLogInResponse().getStatus().getMessage().equals("Logged in as username"));
  }

  @Test
  public void LOG_IN_USER_DOES_NOT_EXIST() {
    ChatMessage message = ChatMessageGenerator.LOG_IN_USER_DOES_NOT_EXIST("username");

    assert(message.getContent().getLogInResponse().getStatus().getCode() == Code.NOT_FOUND.getNumber());
    
    assert(message.getContent().getLogInResponse().getStatus().getMessage().equals("Cannot log in because the user username does not exist"));
  }

  @Test
  public void DISTRIBUTE_MESSAGE() {
    ChatMessage message = ChatMessageGenerator.DISTRIBUTE_MESSAGE("sender", "message");

    assert(message.getContent().getDistributeMessageRequest().getSender().equals("sender"));
    
    assert(message.getContent().getDistributeMessageRequest().getMessage().equals("message"));
  }

  @Test
  public void LOG_OUT_SUCCESS() {
    ChatMessage message = ChatMessageGenerator.LOG_OUT_SUCCESS("username");

    assert(message.getContent().getLogOutResponse().getStatus().getCode() == Code.OK.getNumber());
    
    assert(message.getContent().getLogOutResponse().getStatus().getMessage().equals("Logged out username"));
  }

  @Test
  public void LOG_OUT_USER_NOT_LOGGED_IN() {
    ChatMessage message = ChatMessageGenerator.LOG_OUT_USER_NOT_LOGGED_IN("username");

    assert(message.getContent().getLogOutResponse().getStatus().getCode() == Code.FAILED_PRECONDITION.getNumber());
    
    assert(message.getContent().getLogOutResponse().getStatus().getMessage().equals("Cannot log out because you are not logged in"));
  }

  @Test
  public void SEND_MESSAGE_SUCCESS() {
    ChatMessage message = ChatMessageGenerator.SEND_MESSAGE_SUCCESS("sender", "recipient");

    assert(message.getContent().getSendMessageResponse().getStatus().getCode() == Code.OK.getNumber());
    
    assert(message.getContent().getSendMessageResponse().getStatus().getMessage().equals("Queued message from sender for recipient!"));
  }

  @Test
  public void SEND_MESSAGE_RECIPIENT_DOES_NOT_EXIST() {
    ChatMessage message = ChatMessageGenerator.SEND_MESSAGE_RECIPIENT_DOES_NOT_EXIST("username");

    assert(message.getContent().getSendMessageResponse().getStatus().getCode() == Code.NOT_FOUND.getNumber());
    
    assert(message.getContent().getSendMessageResponse().getStatus().getMessage().equals("Cannot send message to username because that user does not exist"));
  }

  @Test
  public void SEND_MESSAGE_USER_NOT_LOGGED_IN() {
    ChatMessage message = ChatMessageGenerator.SEND_MESSAGE_USER_NOT_LOGGED_IN("username");

    assert(message.getContent().getSendMessageResponse().getStatus().getCode() == Code.FAILED_PRECONDITION.getNumber());
    
    assert(message.getContent().getSendMessageResponse().getStatus().getMessage().equals("Cannot send a message because you're not logged in"));
  }

  @Test
  public void LIST_ACCOUNTS() {
    ArrayList<String> accounts = new ArrayList<String>();
    accounts.add("account1");
    accounts.add("account2");
    accounts.add("account3");

    ChatMessage message = ChatMessageGenerator.LIST_ACCOUNTS(accounts);

    assert(message.getContent().getListAccountsResponse().getAccountsList().get(0).equals("account1"));

    assert(message.getContent().getListAccountsResponse().getAccountsList().get(1).equals("account2"));

    assert(message.getContent().getListAccountsResponse().getAccountsList().get(2).equals("account3"));
  }

  @Test
  public void DELETE_ACCOUNT_USER_DOES_NOT_EXIST() {
    ChatMessage message = ChatMessageGenerator.DELETE_ACCOUNT_USER_DOES_NOT_EXIST("username");

    assert(message.getContent().getDeleteAccountResponse().getStatus().getCode() == Code.NOT_FOUND.getNumber());
    
    assert(message.getContent().getDeleteAccountResponse().getStatus().getMessage().equals("Cannot delete account because the user username does not exist"));
  }

  @Test
  public void DELETE_ACCOUNT_SUCCESS() {
    ChatMessage message = ChatMessageGenerator.DELETE_ACCOUNT_SUCCESS("username");

    assert(message.getContent().getDeleteAccountResponse().getStatus().getCode() == Code.OK.getNumber());
    
    assert(message.getContent().getDeleteAccountResponse().getStatus().getMessage().equals("Deleted account named username"));
  }
}