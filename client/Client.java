package client;

import java.util.Scanner;

import protocol.Constants;
import protocol.Message;

public class Client {
  static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    // listen for new user commands from the console
    while(true) {
      // read the next command from the console and parse it into a Message
      Message message = CommandParser.parseNextCommandToMessage(in);
      if (message == null) {
        System.out.println("Invalid command. Please check the number of arguments.");
        continue;
      }

      // TODO: Handle message
    }
  }
}
