package protocol;

public class Marshaller {
    public static byte[] marshall(Message message) {
        byte[] marshalledMessage = new byte[message.getMessageLength()];
        marshalledMessage[0] = message.getVersion();
        marshalledMessage[1] = message.getMessageLength();
        marshalledMessage[2] = message.getOperation().toByte();
        marshalledMessage[3] = message.getException().toByte();
        System.arraycopy(message.getContent(), 0, marshalledMessage, 4, message.getContent().length);
        return marshalledMessage;
    }

    public static Message unmarshall(byte[] marshalledMessage) {
        byte version = marshalledMessage[0];
        byte messageLength = marshalledMessage[1];
        Operation operation = Operation.fromByte(marshalledMessage[2]);
        Exception exception = Exception.fromByte(marshalledMessage[3]);
        byte[] content = new byte[messageLength - 4];
        System.arraycopy(marshalledMessage, 4, content, 0, content.length);
        return new Message(version, messageLength, operation, exception, content);
    }
}