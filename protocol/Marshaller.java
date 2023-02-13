package protocol;

import java.util.ArrayList;

public class Marshaller {
    public static byte[] marshall(Message message) {
        byte[] marshalledMessage = new byte[message.getMessageLength()];
        marshalledMessage[Constants.VERSION_POSITION] =
          message.getVersion();
        marshalledMessage[Constants.MESSAGE_LENGTH_POSITION] =
          message.getMessageLength();
        marshalledMessage[Constants.OPERATION_POSITION] =
          message.getOperation().toByte();
        marshalledMessage[Constants.EXCEPTION_POSITION] =
          message.getException().toByte();

        // escape special characters before marshalling
        byte[] escapedContent = escapeRestrictedCharacters(message.getContent());
        
        System.arraycopy(escapedContent, 0, marshalledMessage, Constants.CONTENT_POSITION, message.getContent().length);
        
        return marshalledMessage;
    }

    public static Message unmarshall(byte[] marshalledMessage) {
        byte version =
          marshalledMessage[Constants.VERSION_POSITION];
        byte messageLength =
          marshalledMessage[Constants.MESSAGE_LENGTH_POSITION];
        Operation operation =
          Operation.fromByte(marshalledMessage[Constants.OPERATION_POSITION]);
        Exception exception =
          Exception.fromByte(marshalledMessage[Constants.EXCEPTION_POSITION]);

        // unescape special characters after unmarshalling
        byte[] content =
          unescapeRestrictedCharacters(
            new byte[messageLength - Constants.CONTENT_POSITION]
          );

        return new Message(version, messageLength, operation, exception, content);
    }

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
        output.add(b);
      }
      // convert ArrayList<Byte> to byte[]
      byte[] outputArray = new byte[output.size()];
      for (int i = 0; i < output.size(); i++) {
        outputArray[i] = output.get(i);
      }
      return outputArray;
    }

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