package server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import protocol.Constants;
import protocol.Message;

public class ServerHandler implements Runnable {

    // Necessary to define run parameters for a thread
    private Message message;

    public ServerHandler(Message message) {
        this.message = message;
    }

    // Delegates the message to the correct handler
    public static void run() {
        byte operation = message.getOperation();
        switch (operation) {
            case 0:
                byte[] content = message.getContent();
                createAccountHandler(content);
                break;
            case 1:
                byte[] content = message.getContent();
                loginHandler(content);
                break;
            case 2:
                byte[] content = message.getContent();
                byte[][] splitContent = splitByteArray(content, Constants.ARGUMENT_SEPARATOR);
                byte[] recipient = splitContent[0];
                UUID messageID = asUuid(splitContent[1]);
                byte[] message = splitContent[2];
                sendMessageHandler(recipient, messageID, message);
                break;
        }
    }

    // Utility functions for byte array parsing
    private static byte[][] splitByteArray(byte[] array, byte separator) {
        int separatorCount = 0;
        for (byte b : array) {
            if (b == separator) {
                separatorCount++;
            }
        }

        byte[][] result = new byte[separatorCount + 1][];
        int index = 0;
        int startIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == separator) {
                result[index] = Arrays.copyOfRange(array, startIndex, i);
                startIndex = i + 1;
                index++;
            }
        }
        result[index] = Arrays.copyOfRange(array, startIndex, array.length);
        return result;
    }

    private static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    // Sender-related functions
    private static void createAccountHandler(byte[] username) {
        
    }

    private static void loginHandler(byte[] username) {

    }

    private static void sendMessageHandler(byte[] recipient, UUID messageID, byte[] message) {

    }

    // Recipient-related functions
}