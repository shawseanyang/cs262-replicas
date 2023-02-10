package server;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import protocol.Operation;
import java.util.Arrays;
import java.util.UUID;

import java.lang.Exception;
import protocol.*;
import protocol.Constants;
import protocol.Message;

public class ServerHandler implements Runnable {

    // Necessary to define run parameters for a thread
    private Message message;
    private Socket socket;
    
    private User user;

    public ServerHandler(Message message, Socket socket) {
        this.message = message;
        this.socket = socket;
    }

    // Delegates the message to the correct handler
    public void run() {
        Operation operation = message.getOperation();
        byte[] content = message.getContent();
        switch (operation) {
            case CREATE_ACCOUNT:
                createAccountHandler(content);
                break;
            case LOG_IN:
                loginHandler(content);
                break;
            case SEND_MESSAGE:
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
    private void createAccountHandler(byte[] username) {
        
    }

    private void loginHandler(byte[] username) {
        user = new User(username);
        if (Server.clients.containsKey(user)) {
            // If client has already logged in, close the old socket
            Socket oldSocket = Server.clients.get(user);
            if (oldSocket != null) {
                try {
                    oldSocket.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Could not close the socket.");
                    e.printStackTrace();
                }
            }
            
            // Update the socket
            Server.clients.put(user, socket);

            // Send a success message
            Message successMessage = new Message(Constants.CURRENT_VERSION, Constants.CONTENT_POSITION, Operation.LOG_IN_RESPONSE, protocol.Exception.NONE, new byte[0]);
        }
    }

    private void sendMessageHandler(byte[] recipient, UUID messageID, byte[] message) {

    }

    // Recipient-related functions
}