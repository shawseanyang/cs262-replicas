package protocol;

import java.util.ArrayList;

import utility.ByteConverter;

public class Marshaller {
    public static byte[] marshall(Message message) {
        // marshall the arguments first so we know how long the message will be
        byte[] marshalledArguments = marshallArguments(message.getArguments());
        // calculate the length of the entire message by adding the length of the arguments to the length of the "header" portion (everything before the arguments)
        byte[] marshalledMessage = new byte[Constants.CONTENT_POSITION + marshalledArguments.length];

        // fill in the header portion of the message
        marshalledMessage[Constants.VERSION_POSITION] = message.getVersion();
        marshalledMessage[Constants.OPERATION_POSITION] = message.getOperation().toByte();
        marshalledMessage[Constants.EXCEPTION_POSITION] = message.getException().toByte();

        // fill in the content portion of the message
        for (int i = 0; i < marshalledArguments.length; i++) {
            marshalledMessage[Constants.CONTENT_POSITION + i] = marshalledArguments[i];
        }
        
        return marshalledMessage;
    }

    public static Message unmarshall(byte[] marshalledMessage) {
        // unmarshall the header portion
        byte version =
          marshalledMessage[Constants.VERSION_POSITION];
        Operation operation =
          Operation.fromByte(marshalledMessage[Constants.OPERATION_POSITION]);
        Exception exception =
          Exception.fromByte(marshalledMessage[Constants.EXCEPTION_POSITION]);

        // unmarshall the arguments portion
        ArrayList<byte[]> arguments = unmarshallArguments(marshalledMessage);

        return new Message(version, operation, exception, arguments);
    }

    // marshalls arguments by escaping special characters and then concatenating them with the argument separator
    private static byte[] marshallArguments(ArrayList<byte[]> arguments) {
      // parse arguments into an ArrayList of bytes, perform escaping, then convert back to byte[]
      ArrayList<Byte> output = new ArrayList<Byte>();
      for (byte[] argument : arguments) {
        for (byte b : escapeRestrictedCharacters(argument)) {
          output.add(b);
        }
        output.add(Constants.ARGUMENT_SEPARATOR);
      }
      return ByteConverter.ByteArrayListToByteArray(output);
    }

    // TODO: Broken
    private static ArrayList<byte[]> unmarshallArguments(byte[] marshalledMessage) {
      // unescape the arguments and then split them by the argument separator
      ArrayList<byte[]> arguments = new ArrayList<byte[]>();
      byte[] unescapedMessage = unescapeRestrictedCharacters(marshalledMessage);
      int argumentStart = Constants.CONTENT_POSITION;
      for (int i = Constants.CONTENT_POSITION; i < unescapedMessage.length; i++) {
        if (unescapedMessage[i] == Constants.ARGUMENT_SEPARATOR) {
          byte[] argument = new byte[i - argumentStart];
          for (int j = 0; j < argument.length; j++) {
            argument[j] = unescapedMessage[argumentStart + j];
          }
          arguments.add(argument);
          argumentStart = i + 1;
        }
      }
      return arguments;
    }

    // TODO: Broken
    // Escapes restricted characters by adding the escape character in front of them. Ex: \t becomes \\t and \n becomes \\n
    private static byte[] escapeRestrictedCharacters(byte[] input) {
      // parse input into an ArrayList of bytes, perform escaping, then convert back to byte[]
      ArrayList<Byte> output = new ArrayList<Byte>();
      for (byte b : input) {
        if (b == Constants.ARGUMENT_SEPARATOR) {
          output.add(Constants.ESCAPE_CHARACTER);
        } else if (b == Constants.MESSAGE_SEPARATOR) {
          output.add(Constants.ESCAPE_CHARACTER);
        }
        else if (b == Constants.ESCAPE_CHARACTER) {
          output.add(Constants.ESCAPE_CHARACTER);
        }
        output.add(b);
      }
      // convert ArrayList<Byte> to byte[]
      byte[] outputArray = new byte[output.size()];
      for (int i = 0; i < output.size(); i++) {
        outputArray[i] = output.get(i);
      }
      return outputArray;
    }

    // TODO: Broken
    // Unescapes restricted characters by removing the escape character in front of them. Ex: \\t becomes \t and \\n becomes \n
    private static byte[] unescapeRestrictedCharacters(byte[] input) {
      // parse input into an ArrayList of bytes, perform unescaping, then convert back to byte[]
      ArrayList<Byte> output = new ArrayList<Byte>();
      for (int i = 0; i < input.length; i++) {
        if (input[i] == Constants.ESCAPE_CHARACTER) {
          i++;
        }
        output.add(input[i]);
      }
      // convert ArrayList<Byte> to byte[]
      byte[] outputArray = new byte[output.size()];
      for (int i = 0; i < output.size(); i++) {
        outputArray[i] = output.get(i);
      }
      return outputArray;
    }
}