package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import java.lang.Exception;
import protocol.*;
import protocol.Constants;
import protocol.Message;
import protocol.Operation;

import utility.ByteConverter;

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
        protocol.Exception resultingException = validateMessage();
        if (resultingException != protocol.Exception.NONE) {
            // Send an error message
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, message.getOperation(), resultingException, new byte[0]);
            try {
                out.write(Marshaller.marshall(errorMessage));
            } catch (IOException e) {
                System.err.println("ERROR: Could not send the error message.");
                e.printStackTrace();
            }
            return;
        }
        
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
                byte[][] splitContent = ByteConverter.splitByteArray(content, Constants.ARGUMENT_SEPARATOR);
                byte[] recipient = splitContent[0];
                UUID messageID = ByteConverter.byteArrayToUUID(splitContent[1]);
                byte[] message = splitContent[2];
                sendMessageHandler(recipient, messageID, message);
                break;
            case LIST_ACCOUNTS:
                listAccountsHandler(content);
                break;
        }
    }

    private protocol.Exception validateMessage() {
        Operation operation = message.getOperation();
        byte[] content = message.getContent();
        byte[][] splitContent = ByteConverter.splitByteArray(content, Constants.ARGUMENT_SEPARATOR);

        // Check if the message is valid according to the wire protocol
        protocol.Exception resultingException = MessageValidator.validateMessage(message);
        if (resultingException != protocol.Exception.NONE) {
            return resultingException;
        }

        switch (operation) {
            case CREATE_ACCOUNT:
            case LOG_IN:
                // Check if the username is valid (not empty)
                if (content.length == 0) {
                    return protocol.Exception.INVALID_USERNAME;
                }
                break;
            case SEND_MESSAGE:
                // Check if recipient exists
                if (!Server.clients.containsKey(new User(splitContent[0]))) {
                    return protocol.Exception.USER_DOES_NOT_EXIST;
                }
                break;
        }
        return protocol.Exception.NONE;
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
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.SEND_MESSAGE_RESPONSE, protocol.Exception.NOT_LOGGED_IN, new byte[0]);
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

    private void listAccountsHandler(byte[] regex) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            // Send an error message
            Message errorMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.LIST_ACCOUNTS_RESPONSE, protocol.Exception.NOT_LOGGED_IN, new byte[0]);
            try {
                out.write(Marshaller.marshall(errorMessage));
            } catch (IOException e) {
                System.err.println("ERROR: Could not send the error message.");
                e.printStackTrace();
            }
            return;
        }

        // Compare the query to the list of users
        for(User other : Server.clients.keySet()) {
            String otherString = ByteConverter.byteArrayToString(other.getUsername());
        }

        // Send a success message
        Message successMessage = new Message(Constants.CURRENT_VERSION, (byte) Constants.CONTENT_POSITION, Operation.LIST_ACCOUNTS_RESPONSE, protocol.Exception.NONE, accounts);
        try {
            out.write(Marshaller.marshall(successMessage));
        } catch (IOException e) {
            System.err.println("ERROR: Could not send the success message.");
            e.printStackTrace();
        }
    }

    // Recipient-related functions
}