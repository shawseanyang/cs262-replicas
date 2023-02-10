package protocol;

public class Marshaller {
    public static byte[] marshall(Message message) {
        byte[] marshalledMessage = new byte[message.getCharacterLength()];
        marshalledMessage[0] = message.getVersion();
        marshalledMessage[1] = message.getCharacterLength();
        marshalledMessage[2] = message.getOperation();
        marshalledMessage[3] = message.getException();
        System.arraycopy(message.getContent(), 0, marshalledMessage, 4, message.getContent().length);
        return marshalledMessage;
    }

    public static Message unmarshall(byte[] marshalledMessage) {
        byte version = marshalledMessage[0];
        byte characterLength = marshalledMessage[1];
        byte operation = marshalledMessage[2];
        byte exception = marshalledMessage[3];
        byte[] content = new byte[characterLength - 4];
        System.arraycopy(marshalledMessage, 4, content, 0, content.length);
        return new Message(version, characterLength, operation, exception, content);
    }
}