package protocol;

public class Message {
    public byte version;
    public byte characterLength;
    public byte operation;
    public byte exception;
    public byte[] content;
}
