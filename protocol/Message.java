package protocol;

public class Message {
    private byte version;
    private byte messageLength;
    private Operation operation;
    private Exception exception;
    private byte[] content;

    public Message(byte version, byte messageLength, Operation operation, Exception exception, byte[] content) throws IllegalArgumentException {
        this.version = version;
        this.messageLength = messageLength;
        this.operation = operation;
        this.exception = exception;
        this.content = content;

        if (content.length != messageLength - Constants.CONTENT_POSITION) {
            throw new IllegalArgumentException("Content length does not match message length");
        }
    }

    public byte getVersion() {
        return version;
    }
    public byte getMessageLength() {
        return messageLength;
    }
    public Operation getOperation() {
        return operation;
    }
    public Exception getException() {
        return exception;
    }
    public byte[] getContent() {
        return content;
    }
}
