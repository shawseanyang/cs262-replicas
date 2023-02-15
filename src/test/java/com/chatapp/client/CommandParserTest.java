package com.chatapp.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chatapp.client.commands.Command;
import com.chatapp.client.commands.ConnectCommand;
import com.chatapp.client.commands.CreateAccountCommand;
import com.chatapp.client.commands.DeleteAccountCommand;
import com.chatapp.client.commands.ListAccountsCommand;
import com.chatapp.client.commands.LogOutCommand;
import com.chatapp.client.commands.SendMessageCommand;

public class CommandParserTest {
  @Test
  public void parse_emptyCommand_ExceptionThrown() {
    
    try {
      CommandParser.parse("");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_invalidCommand_ExceptionThrown() {
    
    try {
      CommandParser.parse("nonsense");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_logOutCommand_createsLogOutCommand() {
    
    try {
      Command command = CommandParser.parse("logout");
      assertTrue(command instanceof LogOutCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_logInCommand_createsLogInCommand() {
    
    try {
      CommandParser.parse("login alan_turing");
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_logInCommandWithoutUsername_ExceptionThrown() {
    
    try {
      CommandParser.parse("login");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_createAccountCommand_createsCreateAccountCommand() {
    
    try {
      Command command = CommandParser.parse("create_account alan_turing");
      assertTrue(command instanceof CreateAccountCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_createAccountCommandWithoutUsername_ExceptionThrown() {
    
    try {
      CommandParser.parse("create_account");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_deleteAccountCommand_createsDeleteAccountCommand() {
    
    try {
      Command command = CommandParser.parse("delete_account alan_turing");
      assertTrue(command instanceof DeleteAccountCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_deleteAccountCommandWithoutUsername_ExceptionThrown() {
    
    try {
      CommandParser.parse("delete_account");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_listAccountsCommand_createsListAccountsCommand() {
    
    try {
      Command command = CommandParser.parse("list_accounts alan_*");
      assertTrue(command instanceof ListAccountsCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_listAccountsCommandWithoutPattern_ExceptionThrown() {
    
    try {
      CommandParser.parse("list_accounts");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_sendMessageCommand_createsSendMessageCommand() {
    
    try {
      Command command = CommandParser.parse("send alan_turing hello");
      assertTrue(command instanceof SendMessageCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_sendMessageCommandWithOnlyOneArgument_ExceptionThrown() {
    
    try {
      CommandParser.parse("send alan_turing");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_sendMessageCommandWithoutArguments_ExceptionThrown() {
    
    try {
      CommandParser.parse("send");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_connectCommand_createsConnectCommand() {
    
    try {
      Command command = CommandParser.parse("connect localhost 8080");
      assertTrue(command instanceof ConnectCommand);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void parse_connectCommandWithOnlyOneArgument_ExceptionThrown() {
    
    try {
      CommandParser.parse("connect localhost");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_connectCommandWithoutArguments_ExceptionThrown() {
    
    try {
      CommandParser.parse("connect");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  @Test
  public void parse_connectCommandWithInvalidPort_ExceptionThrown() {
    
    try {
      CommandParser.parse("connect localhost 70000");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }
}