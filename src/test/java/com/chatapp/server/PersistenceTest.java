package com.chatapp.server;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chatapp.server.Persistence.AccountSerializer;
import com.chatapp.server.Persistence.MessageSerializer;
import com.chatapp.server.Persistence.SerializerUtil;
import com.chatapp.server.Persistence.SerializerUtil.TextType;

public class PersistenceTest {

    static final int REPLICA_NUMBER = 1;
    static final Replica REPLICA = Replica.REPLICAS[REPLICA_NUMBER];
    static final String TEST_USERNAME = "test";
    static final PendingMessage TEST_MESSAGE = new PendingMessage(TEST_USERNAME, TEST_USERNAME, "1");

    /*
     * Set up the replica folder and start the business logic server
     */
    @Before
    public void setUp() {
        // start the Bully algorithm for this replica
        ReplicaManager replicaManager = new Bully(REPLICA);
        // grab the port for this replica's business logic server
        int port = com.chatapp.protocol.Server.SERVERS[REPLICA_NUMBER].getPort();
        // create the business logic server
        BusinessLogicServer businessLogicServer = new BusinessLogicServer(replicaManager, port);

        // if exists, delete the replica folder
        File folder = new File(BusinessLogicServer.getReplicaFolder());
        if(folder.exists()) {
            String[] entries = folder.list();
            for(String s: entries){
                File currentFile = new File(folder.getPath(),s);
                currentFile.delete();
            }
            folder.delete();
        }

        // create a folder for the replica
        new java.io.File(BusinessLogicServer.getReplicaFolder()).mkdirs();
    }

    /*
     * Delete the replica folder
     */
    @After
    public void tearDown() {
        // delete the replica folder
        File folder = new File(BusinessLogicServer.getReplicaFolder());
        if(folder.exists()) {
            String[] entries = folder.list();
            for(String s: entries){
                File currentFile = new File(folder.getPath(),s);
                currentFile.delete();
            }
            folder.delete();
        }
    }

    /*
     * Test that serializing an account and then deserializing the account obtains the account
     */
    @Test
    public void SerializingAccount_then_DeserializingAccount_obtains_Username() {
        // serialize an account
        AccountSerializer.serialize(TEST_USERNAME);
        // deserialize and get the first line
        String line = AccountSerializer.deserialize().get(0);
        // check that the first line contains the account
        assert(line.equals(TEST_USERNAME));
    }

    /*
     * Test that serializing an account and then updating the account file changes the file
     */
    @Test
    public void SerializingAccounts_then_UpdatingAccounts_updates_Files() {
        // create the account
        AccountSerializer.serialize(TEST_USERNAME);
        // delete the account
        AccountSerializer.serialize(TEST_USERNAME);
        // create the account
        AccountSerializer.serialize(TEST_USERNAME);

        // update the accounts
        AccountSerializer.updateAccounts();

        // check that the accounts file contains only one listing of the account
        int length = SerializerUtil.read(TextType.ACCOUNT).size();
        assert(length == 1);


        // delete the account
        AccountSerializer.serialize(TEST_USERNAME);

        // update the accounts
        AccountSerializer.updateAccounts();

        // check that the accounts file contains no listing of the account
        length = SerializerUtil.read(TextType.ACCOUNT).size();
        assert(length == 0);
    }

    /*
     * Test that serializing a message and then deserializing the message obtains the message
     */
    @Test
    public void SerializingMessage_then_DeserializingMessage_obtains_Message() {
        // send a message
        MessageSerializer.serialize(TEST_MESSAGE);
        // read the message
        PendingMessage result = MessageSerializer.deserialize().get(0);
        // check that the first line contains the same message
        assert(result.getSender().equals(TEST_MESSAGE.getSender()));
        assert(result.getRecipient().equals(TEST_MESSAGE.getRecipient()));
        assert(result.getMessage().equals(TEST_MESSAGE.getMessage()));
    }

    /*
     * Test that serializing a message and then updating the message file changes the file
     */
    @Test
    public void SerializingMessage_then_UpdatingMessages_then_deletingAccount_deletes_Message() {
        // create an account
        AccountSerializer.serialize(TEST_USERNAME);
        // send a message
        MessageSerializer.serialize(TEST_MESSAGE);
        // update the accounts
        AccountSerializer.updateAccounts();
        // update the messages
        MessageSerializer.updateMessages();
        // check that the messages file contains the message
        int length = SerializerUtil.read(TextType.MESSAGE).size();
        assert(length == 1);

        // delete the account
        AccountSerializer.serialize(TEST_USERNAME);
        // update the accounts
        AccountSerializer.updateAccounts();
        // update the messages
        MessageSerializer.updateMessages();
        // check that the messages file contains no messages (because the user no longer exists)
        length = SerializerUtil.read(TextType.MESSAGE).size();
        assert(length == 0);
    }

    /*
     * Test that writing to a file and then reading the file obtains the same data
     */
    @Test
    public void Writing_then_Reading_produces_Username() {
        // Write to the accounts file
        String[] args = {TEST_USERNAME};
        SerializerUtil.write(TextType.ACCOUNT, args);

        // check that the first line contains the account
        String contents = SerializerUtil.read(TextType.ACCOUNT).get(0);
        assert(contents.equals(TEST_USERNAME));
    }

}
