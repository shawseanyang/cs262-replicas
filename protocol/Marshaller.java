package protocol;

public class Marshaller {
    public static byte[] marshall(Message message) {
        byte[] marshalledMessage = new byte[message.characterLength];
        marshalledMessage[0] = message.version;
        marshalledMessage[1] = message.characterLength;
        marshalledMessage[2] = message.operation;
        marshalledMessage[3] = message.exception;
        System.arraycopy(message.content, 0, marshalledMessage, 4, message.content.length);
        return marshalledMessage;
    }

    public static Message unmarshall(byte[] message) {
        Message unmarshalledMessage = new Message();
        unmarshalledMessage.version = message[0];
        unmarshalledMessage.characterLength = message[1];
        unmarshalledMessage.operation = message[2];
        unmarshalledMessage.exception = message[3];
        unmarshalledMessage.content = new byte[unmarshalledMessage.characterLength - 4];
        System.arraycopy(message, 4, unmarshalledMessage.content, 0, unmarshalledMessage.content.length);
        return unmarshalledMessage;
    }
}