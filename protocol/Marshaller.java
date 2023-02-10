package protocol;

public class Marshaller {
    public static byte[] marshall(Message message) {
        byte[] marshalledMessage = new byte[message.getMessageLength()];
        marshalledMessage[Constants.VERSION_POSITION] =
          message.getVersion();
        marshalledMessage[Constants.MESSAGE_LENGTH_POSITION] =
          message.getMessageLength();
        marshalledMessage[Constants.OPERATION_POSITION] =
          message.getOperation().toByte();
        marshalledMessage[Constants.CONTENT_POSITION] =
          message.getException().toByte();
        
        System.arraycopy(message.getContent(), 0, marshalledMessage, Constants.CONTENT_POSITION, message.getContent().length);
        
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
        byte[] content =
          new byte[messageLength - Constants.CONTENT_POSITION];
        
        System.arraycopy(marshalledMessage, Constants.CONTENT_POSITION, content, 0, content.length);

        return new Message(version, messageLength, operation, exception, content);
    }
}