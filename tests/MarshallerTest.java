package tests;

import java.lang.Exception;
import java.util.ArrayList;
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
    Operation operation = Operation.SEND_MESSAGE;
    protocol.Exception exception = protocol.Exception.NONE;

    // define the content, convert it to bytes, then grab the length
    String contentString = "hello world";
    byte[] contentBytes = contentString.getBytes();
    ArrayList<byte[]> args = new ArrayList<byte[]>();
    args.add(contentBytes);

    Message message = new Message(version, operation, exception, args);
    byte[] marshalledMessage = Marshaller.marshall(message);

    Message unmarshalledMessage = Marshaller.unmarshall(marshalledMessage);

    assert message.getVersion() == unmarshalledMessage.getVersion();
    assert message.getOperation() == unmarshalledMessage.getOperation();
    assert message.getException() == unmarshalledMessage.getException();
    assert message.getArguments().equals(unmarshalledMessage.getArguments());
  }
}
