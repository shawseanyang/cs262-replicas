package com.chatapp.server.Persistence;

import com.chatapp.server.BusinessLogicServer;
import com.chatapp.server.PendingMessage;
import com.chatapp.server.Persistence.SerializerUtil.TextType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MessageSerializer {

    /*
     * Writes the message to a file
     * @param message the message to write to the file
     */
    public static void serialize(PendingMessage message) {
        String[] arguments = {message.getRecipient(), message.getSender(), message.getMessage()};
        
        // Write marshalled message to message file and backup file
        SerializerUtil.write(TextType.MESSAGE, arguments);
    }

    /*
     * Reads the file and returns a list of messages
     * @return a list of messages
     */
    public static ArrayList<PendingMessage> deserialize() {
        // Get a list of marshalled messages
        List<String> messages = SerializerUtil.read(TextType.MESSAGE);

        // Create an ArrayList of PendingMessages
        ArrayList<PendingMessage> pendingMessages = new ArrayList<PendingMessage>();

        // Unmarshall each message and add it to the list
        for (String message : messages) {
            ArrayList<String> arguments = SerializerUtil.unmarshallArguments(message);
            if (arguments.size() == 3)
                pendingMessages.add(new PendingMessage(arguments.get(0), arguments.get(1), arguments.get(2)));
        }

        return pendingMessages;
    }

    /*
     * Cleans the message file of messages from deleted accounts, 
     * writes the updated messages to the message file, and
     * copies the updated message file to the backup file
     */
    public static void updateMessages() {
        // Get a list of messages
        ArrayList<PendingMessage> messages = deserialize();

        // Load the past accounts into a HashSet for O(1) lookup time
        List<String> pastAccounts = SerializerUtil.read(TextType.ACCOUNT);
        HashSet<String> accounts = new HashSet<String>(pastAccounts);

        // Clear the message file at the last possible time
        SerializerUtil.clear(BusinessLogicServer.getReplicaFolder() + Constants.MESSAGE_FILE);
      
        // Only write the past messages to the message file for which both
        // the sender and recipient are still active
        for (PendingMessage message : messages) {
            if(accounts.contains(message.getRecipient()) && accounts.contains(message.getSender())) {
                serialize(message);
            }
        }

        // Copy to backup file as soon as possible
        SerializerUtil.copy(BusinessLogicServer.getReplicaFolder() + Constants.MESSAGE_FILE, BusinessLogicServer.getReplicaFolder() + Constants.MESSAGE_BACKUP_FILE);
    }

}
