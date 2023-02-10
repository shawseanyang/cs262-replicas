package protocol;

public class Message {
    private byte version;
    private byte messageLength;
    private byte operation;
    private byte exception;
    private byte[] content;

    public Message(byte version, byte characterLength, byte operation, byte exception, byte[] content) {
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
    public byte getOperation() {
        return operation;
    }
    public byte getException() {
        return exception;
    }
    public byte[] getContent() {
        return content;
    }
}
