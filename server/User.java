package server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class User {

    static class QueuedMessage {
        private User sender;
        private byte[] message;
        private UUID id;

        public QueuedMessage(User sender, byte[] message, UUID id) {
            this.sender = sender;
            this.id = id;
            this.message = message;
        }
    }

    private byte[] username;
    private Queue<QueuedMessage> messages;
    private HashSet<UUID> undeliveredMessages;

    public User(byte[] username) {
        this.username = username;
        this.messages = new LinkedList<QueuedMessage>();
        this.undeliveredMessages = new HashSet<UUID>();
    }

    public byte[] getUsername() {
        return username;
    }

    public User getSender() {
        return messages.peek().sender;
    }

    public byte[] getMessage() {
        return messages.peek().message;
    }

    public UUID getMessageId() {
        return messages.peek().id;
    }

    public void addMessage(User sender, byte[] message, UUID messageID) {
        // Don't add duplicate messages
        if (undeliveredMessages.contains(messageID))
            return;
        
        messages.add(new QueuedMessage(sender, message, messageID));
        undeliveredMessages.add(messageID);
    }

    public QueuedMessage removeMessage() {
        QueuedMessage message = messages.poll();
        undeliveredMessages.remove(message.id);
        return message;
    }

    // TODO: Edit to regex match wildcards
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Arrays.equals(username, user.username);
    }
}