package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            sendResponseMessage(resultingException);
            return;
        }
        
        Operation operation = message.getOperation();
        ArrayList<byte[]> args = message.getArguments();
        switch (operation) {
            case CREATE_ACCOUNT:
                createAccountHandler(args.get(0));
                break;
            case LOG_IN:
                loginHandler(args.get(0));
                break;
            case SEND_MESSAGE:
                byte[] recipient = args.get(0);
                byte[] message = args.get(1);
                UUID messageID = ByteConverter.byteArrayToUUID(args.get(2));
                sendMessageHandler(recipient, message, messageID);
                break;
            case LIST_ACCOUNTS:
                listAccountsHandler(args.get(0));
                break;
        }
    }

    /* Sender-related functions */
    
    private void createAccountHandler(byte[] username) {
        user = new User(username);

        // If username already exists, send an error message
        if (!Server.clients.containsKey(user)) {
            sendResponseMessage(protocol.Exception.USER_ALREADY_EXISTS);
        }
        
        // Add the socket
        Server.clients.put(user, socket);

        // Send a success message
        sendResponseMessage(protocol.Exception.NONE);
    }

    private void loginHandler(byte[] username) {
        user = new User(username);

        // If username does not exist, send an error message
        if (!Server.clients.containsKey(user)) {
            sendResponseMessage(protocol.Exception.USER_DOES_NOT_EXIST);
        }

        // If client has already logged in, close the old socket
        Socket oldSocket = Server.clients.get(user);
        if (oldSocket != null && oldSocket != socket) {
            try {
                oldSocket.close();
            } catch (IOException e) {
                System.err.println("ERROR: Could not close the socket.");
                e.printStackTrace();
            }
        }
        
        // Update the socket
        if (oldSocket != socket)
            Server.clients.put(user, socket);

        // Send a success message
        sendResponseMessage(protocol.Exception.NONE);
    }

    private void sendMessageHandler(byte[] recipient, byte[] message, UUID messageID) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            sendResponseMessage(protocol.Exception.NOT_LOGGED_IN);
            return;
        }

        User recipientUser = new User(recipient);

        // Add message to recipient's queue
        recipientUser.addMessage(user, message, messageID);

        // Send a success message
        sendResponseMessage(protocol.Exception.NONE);
    }

    private void listAccountsHandler(byte[] regex) {
        // Check if user is logged in
        if (!isLoggedIn()) {
            sendResponseMessage(protocol.Exception.NOT_LOGGED_IN);
            return;
        }

        // Compile the regex
        Pattern regexString = Pattern.compile(ByteConverter.byteArrayToString(regex));

        // Compare the query to the list of users
        ArrayList<byte[]> matchedUsers = new ArrayList<byte[]>();
        for(User other : Server.clients.keySet()) {
            String otherString = ByteConverter.byteArrayToString(other.getUsername());
            Matcher matcher = regexString.matcher(otherString);

            // If the regex matches the username, add the username
            if (matcher.find()) {
                matchedUsers.add(other.getUsername());
            }
        }

        // Send a success message
        sendResponseMessage(protocol.Exception.NONE, matchedUsers);
    }

    /* Recipient-related functions */



    /* Utility functions */

    // Utility function for validating the message
    private protocol.Exception validateMessage() {
        Operation operation = message.getOperation();
        ArrayList<byte[]> args = message.getArguments();

        // Check if the message is valid according to the wire protocol
        protocol.Exception resultingException = MessageValidator.validateMessage(message);
        if (resultingException != protocol.Exception.NONE) {
            return resultingException;
        }

        switch (operation) {
            case CREATE_ACCOUNT:
            case LOG_IN:
                // Check if the username is valid (not empty)
                if (args.get(0).length == 0) {
                    return protocol.Exception.INVALID_USERNAME;
                }
                break;
            case SEND_MESSAGE:
                // Check if recipient exists
                if (!Server.clients.containsKey(new User(args.get(0)))) {
                    return protocol.Exception.USER_DOES_NOT_EXIST;
                }
                break;
        }
        return protocol.Exception.NONE;
    }

    // Utility functions for sending response messages to the client
    private boolean sendResponseMessage(protocol.Exception exception, ArrayList<byte[]> args) {
        Message responseMessage = new Message(Constants.CURRENT_VERSION, message.getOperation(), exception, args);
        try {
            out.write(Marshaller.marshall(responseMessage));
        } catch (IOException e) {
            if (exception == protocol.Exception.NONE)
                System.err.println("ERROR: Could not send the success message.");
            else
                System.err.println("ERROR: Could not send the error message.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean sendResponseMessage(protocol.Exception exception) {
        return sendResponseMessage(exception, new ArrayList<byte[]>());
    }

    // Utility function for determining if a user is logged in
    private boolean isLoggedIn() {
        return Server.clients.containsKey(user) && Server.clients.get(user) != null;
    }
}