package server;

import java.io.DataOutputStream;
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
    private DataOutputStream out;

    public ServerHandler(Message message, Socket socket, DataOutputStream out) {
        this.message = message;
        this.socket = socket;
        this.out = out;
    }

    // Delegates the message to the correct handler
    public void run() {
        // Validate the message
        if (!messageValidator())
            return;
        
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

    private boolean messageValidator() {
        Operation operation = message.getOperation();
        byte[] content = message.getContent();
        switch (operation) {
            case CREATE_ACCOUNT:
            case LOG_IN:
                // Check if the username is valid (not empty)
                if (content.length == 0) {
                    // Send an error message
                    Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.CREATE_ACCOUNT_RESPONSE, protocol.Exception.INVALID_USERNAME, new byte[0]);
                    try {
                        out.write(Marshaller.marshall(errorMessage));
                    } catch (IOException e) {
                        System.err.println("ERROR: Could not send the error message.");
                        e.printStackTrace();
                    }
                    return false;
                }
                break;
            case SEND_MESSAGE:
                byte[][] splitContent = splitByteArray(content, Constants.ARGUMENT_SEPARATOR);

                // Check if there are 3 arguments
                if (splitContent.length != 3) {
                    // Send an error message
                    Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.SEND_MESSAGE_RESPONSE, protocol.Exception.INVALID_ARGUMENT_NUM, new byte[0]);
                    try {
                        out.write(Marshaller.marshall(errorMessage));
                    } catch (IOException e) {
                        System.err.println("ERROR: Could not send the error message.");
                        e.printStackTrace();
                    }
                    return false;
                }

                // Check if recipient exists
                byte[] recipient = splitContent[0];
                User recipientUser = new User(recipient);
                if (!Server.clients.containsKey(recipientUser)) {
                    // Send an error message
                    Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.SEND_MESSAGE_RESPONSE, protocol.Exception.USER_DOES_NOT_EXIST, new byte[0]);
                    try {
                        out.write(Marshaller.marshall(errorMessage));
                    } catch (IOException e) {
                        System.err.println("ERROR: Could not send the error message.");
                        e.printStackTrace();
                    }
                    return false;
                }
                break;
        }
        return true;
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

    // Utility function for determining if a user is logged in
    private boolean isLoggedIn() {
        return Server.clients.containsKey(user) && Server.clients.get(user) != null;
    }

    // Sender-related functions
    private void createAccountHandler(byte[] username) {
        user = new User(username);

        // If username already exists, send an error message
        if (!Server.clients.containsKey(user)) {
            // Send an error message
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.CREATE_ACCOUNT_RESPONSE, protocol.Exception.USER_ALREADY_EXISTS, new byte[0]);
            try {
                out.write(Marshaller.marshall(errorMessage));
            } catch (IOException e) {
                System.err.println("ERROR: Could not send the error message.");
                e.printStackTrace();
            }
        }
        
        // Add the socket
        Server.clients.put(user, socket);

        // Send a success message
        Message successMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.CREATE_ACCOUNT_RESPONSE, protocol.Exception.NONE, new byte[0]);
        try {
            out.write(Marshaller.marshall(successMessage));
        } catch (IOException e) {
            System.err.println("ERROR: Could not send the success message.");
            e.printStackTrace();
        }
    }

    private void loginHandler(byte[] username) {
        user = new User(username);

        // If username does not exist, send an error message
        if (!Server.clients.containsKey(user)) {
            // Send an error message
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.LOG_IN_RESPONSE, protocol.Exception.USER_DOES_NOT_EXIST, new byte[0]);
            try {
                out.write(Marshaller.marshall(errorMessage));
            } catch (IOException e) {
                System.err.println("ERROR: Could not send the error message.");
                e.printStackTrace();
            }
        }

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
        Message successMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.LOG_IN_RESPONSE, protocol.Exception.NONE, new byte[0]);
        try {
            out.write(Marshaller.marshall(successMessage));
        } catch (IOException e) {
            System.err.println("ERROR: Could not send the success message.");
            e.printStackTrace();
        }
    }

    private void sendMessageHandler(byte[] recipient, UUID messageID, byte[] message) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            // Send an error message
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.SEND_MESSAGE_RESPONSE, protocol.Exception.USER_NOT_LOGGED_IN, new byte[0]);
            try {
                out.write(Marshaller.marshall(errorMessage));
            } catch (IOException e) {
                System.err.println("ERROR: Could not send the error message.");
                e.printStackTrace();
            }
            return;
        }

        User recipientUser = new User(recipient);

        // Add message to recipient's queue
        recipientUser.addMessage(user, messageID, message);

        // Send a success message
        Message successMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.SEND_MESSAGE_RESPONSE, protocol.Exception.NONE, new byte[0]);
        try {
            out.write(Marshaller.marshall(successMessage));
        } catch (IOException e) {
            System.err.println("ERROR: Could not send the success message.");
            e.printStackTrace();
        }
    }

    // Recipient-related functions
}