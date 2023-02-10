package protocol;

public class Constants {
    public final static int PORT = 8080;
    public final static int CURRENT_VERSION = 1;
    public final static byte ARGUMENT_SEPARATOR = 9;
    public final static byte MESSAGE_SEPARATOR = 10;

    // starting positions for each part of the message
    public final static int VERSION_POSITION = 0;
    public final static int MESSAGE_LENGTH_POSITION = 1;
    public final static int OPERATION_POSITION = 2;
    public final static int EXCEPTION_POSITION = 3;
    public final static int CONTENT_POSITION = 4;
}
