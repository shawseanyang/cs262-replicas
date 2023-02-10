package server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class User {
    private byte[] username;
    private Queue<byte[]> messages;

    public User(byte[] username) {
        this.username = username;
        this.messages = new LinkedList<byte[]>();
    }

    public byte[] getUsername() {
        return username;
    }

    public byte[] getMessage() {
        return messages.peek();
    }

    public void addMessage(byte[] message) {
        messages.add(message);
    }

    public byte[] removeMessage() {
        return messages.poll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Arrays.equals(username, user.username);
    }
}