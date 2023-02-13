package client;

import java.util.ArrayList;
import java.util.Scanner;

import protocol.Constants;
import protocol.Message;
import protocol.MessageValidator;
import utility.ByteConverter;

public class CommandParser {
  
  // read the next command from the terminal
  // returns null if there was an error parsing the command
  public static Message parseNextCommandToMessage(Scanner in) {
    String command = in.nextLine();
    String[] commandParts = command.split(" ");
    byte version = Constants.CURRENT_VERSION;
    protocol.Operation operation = parseCommandToOperation(commandParts[1]);
    protocol.Exception exception = protocol.Exception.NONE;

    // the content is the rest of the command string after the operation
    byte[] content = ByteConverter.stringToByteArray(
      command.substring(commandParts[1].length() + 1)
      + (
        // if the operation is SEND_MESSAGE
        operation == protocol.Operation.SEND_MESSAGE
        // then append a UUID as the messageID
        ? Constants.ARGUMENT_SEPARATOR + java.util.UUID.randomUUID().toString()
        : ""
      )
    );

    ArrayList<byte[]> args = new ArrayList<byte[]>();
    args.add(content);

    // create a new message
    Message message = new Message(version, operation, exception, args);
    
    // validate that the right number of arguments were provided
    if (MessageValidator.validateMessage(message) != protocol.Exception.NONE) {
      return null;
    } else {
      return message;
    }
  }

  // parse a command's operation string into an Operation
  private static protocol.Operation parseCommandToOperation(String operation_string) {
    switch (operation_string) {
      case "create_account":
        return protocol.Operation.CREATE_ACCOUNT;
      case "log_in":
        return protocol.Operation.LOG_IN;
      case "log_out":
        return protocol.Operation.LOG_OUT;
      case "send_message":
        return protocol.Operation.SEND_MESSAGE;
      case "list_accounts":
        return protocol.Operation.LIST_ACCOUNTS;
      case "delete_account":
        return protocol.Operation.DELETE_ACCOUNT;
      default:
        return null;
    }
  }
}
