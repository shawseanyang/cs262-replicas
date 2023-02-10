package tests;

import java.lang.Exception;
import protocol.*;

public class MarshallerTest {
  public static void main(String[] args) {
    try {
      testMarshall();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("MarshallerTest finished.");
    }
  }

  public static void testMarshall() throws Exception {
    byte version = 23;
    byte messageLength = 100;
    Operation operation = Operation.SEND_MESSAGE;
    protocol.Exception exception = protocol.Exception.NONE;
    byte[] content = new byte[1];
    content[0] = 1;

    Message message = new Message(version, messageLength, operation, exception, content);
    byte[] marshalledMessage = Marshaller.marshall(message);

    Message unmarshalledMessage = Marshaller.unmarshall(marshalledMessage);

    assert message.getVersion() == unmarshalledMessage.getVersion();
    assert message.getMessageLength() == unmarshalledMessage.getMessageLength();
    assert message.getOperation() == unmarshalledMessage.getOperation();
    assert message.getException() == unmarshalledMessage.getException();
    assert message.getContent() == unmarshalledMessage.getContent();
  }
}
