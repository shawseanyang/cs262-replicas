package com.chatapp.server.Persistence;

import com.chatapp.server.BusinessLogicServer;
import com.chatapp.server.Persistence.SerializerUtil.TextType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AccountSerializer {

    /*
     * Writes the account to a file
     * @param username the account to write to the file
     */
    public static void serialize(String username) {
        String[] arguments = {username};
        
        // Write marshalled account to account file and backup file
        SerializerUtil.write(TextType.ACCOUNT, arguments);
    }

    /*
     * Reads the file and returns a list of live accounts
     * @return a list of live accounts
     */
    public static ArrayList<String> deserialize() {
        // Since accounts that are already created should not be recreated,
        // an odd frequency of an account means that the account is live
        // and an even frequency means that the account is dead
        HashSet<String> liveAccounts = new HashSet<String>();

        // Get a list of accounts
        List<String> accounts = SerializerUtil.read(TextType.ACCOUNT);

        // Determine which accounts are live
        for (String marshalledAccount : accounts) {
            // Unmarshall the account
            ArrayList<String> arguments = SerializerUtil.unmarshallArguments(marshalledAccount);

            if (arguments.size() == 1) {
                String account = arguments.get(0);

                if (liveAccounts.contains(account)) {
                    liveAccounts.remove(account);
                } else {
                    liveAccounts.add(account);
                }
            }
        }

        // Return a list of live accounts
        return new ArrayList<String>(liveAccounts);
    }

    /*
     * Clean the account file and write the updated accounts to the account file
     */
    public static void updateAccounts() {
        HashMap<String, Integer> accountMap = new HashMap<String, Integer>();

        // Get a list of accounts
        List<String> accounts = SerializerUtil.read(TextType.ACCOUNT);

        // Determine the frequency of accounts
        for (String account : accounts) {
            if (accountMap.containsKey(account)) {
                accountMap.put(account, accountMap.get(account) + 1);
            } else {
                accountMap.put(account, 1);
            }
        }

        // Remove accounts that must be fully deleted from the account file
        for (Map.Entry<String, Integer> e : accountMap.entrySet()) {
            Integer frequency = e.getValue();

            // Eliminate accounts that have an even frequency
            if (frequency % 2 == 0) {
                e.setValue(0);
            }
        }

        // Clear the account file at the last possible time
        SerializerUtil.clear(BusinessLogicServer.getReplicaFolder() + Constants.ACCOUNT_FILE);

        // Write the updated accounts to the account file
        for (String account : accounts) {
            if (accountMap.get(account) == 1) {
                serialize(account);
            }
            // Decrement the frequency of the account
            accountMap.put(account, accountMap.get(account) - 1);
        }

        // Copy the account file to the backup file
        SerializerUtil.copy(BusinessLogicServer.getReplicaFolder() + Constants.ACCOUNT_FILE, BusinessLogicServer.getReplicaFolder() + Constants.ACCOUNT_BACKUP_FILE);
    }

}
