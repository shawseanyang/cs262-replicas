package protocol;

public class Message {
    private byte version;
    private byte messageLength;
    private Operation operation;
    private Exception exception;
    private byte[] content;

    public Message(byte version, byte characterLength, Operation operation, Exception exception, byte[] content) {
        this.version = version;
        this.messageLength = characterLength;
        this.operation = operation;
        this.exception = exception;
        this.content = content;
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
