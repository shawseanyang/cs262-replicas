package protocol;

import java.util.ArrayList;

public class Message {
    private byte version;
    private Operation operation;
    private Exception exception;
    private ArrayList<byte[]> arguments;

    public Message(byte version, Operation operation, Exception exception, ArrayList<byte[]> arguments) throws IllegalArgumentException {
        this.version = version;
        this.operation = operation;
        this.exception = exception;
        this.arguments = arguments;
    }

    public byte getVersion() {
        return version;
    }
    public Operation getOperation() {
        return operation;
    }
    public Exception getException() {
        return exception;
    }
    public ArrayList<byte[]> getArguments() {
        return arguments;
    }
}
