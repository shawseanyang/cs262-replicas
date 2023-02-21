package com.chatapp.integration;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.chatapp.client.Client;
import com.chatapp.server.ChatServer;

public class IntegrationTests {
    final static String folderPath = "./src/test/java/com/chatapp/integration/";
    final static String outputFile = "test.out";
    final static String errorFile = "test.err";
    final static boolean ignoreServerOutput = true;
    final static boolean ignoreStackTrace = true;
    Thread serverThread;

    class RunUser implements Runnable {

        @Override
        public void run() {
            try {
                ChatServer.main(new String[0]);
            } catch (IOException e) {
                System.err.println("Server crashed.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Server interrupted.");
                e.printStackTrace();
            }

            while(true);
        }
    }

    /* Server setup */

    @Before
    public void setup() {
        serverThread = new Thread(new RunUser());

        // run server
        serverThread.start();
    }

    @After
    public void teardown() {
        // close server
        serverThread.interrupt();
    }

    /* Specification Tests */

    // 1. Create an account
    @Test
    public void createAccount() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    // You must supply a unique user name.
    @Test
    public void createAccounts_then_UsernameAlreadyExists() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    // 2. List accounts (or a subset of the accounts, by text wildcard)
    @Test
    public void createAccounts_then_ListAccounts_NoMatches() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    @Test
    public void createAccounts_then_ListAccounts_MatchesAll() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    @Test
    public void createAccounts_then_ListAccounts_MatchesSome() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    // 3. Send a message to a recipient

    // If the recipient is logged in, deliver immediately
    @Test
    public void createAccounts_then_LogIn_then_SendMessage_then_LogIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    // Otherwise, queue the message and deliver on demand
    @Test
    public void createAccount_then_LogIn_then_SendMessage() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    // If the message is sent to someone who isn't a user, return an error message
    @Test
    public void createAccount_then_LogIn_then_SendMessage_then_UserDoesNotExist() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    // 4. Deliver undelivered messages to a particular user
    // Undelivered messages are automatically delivered when any user sends any request to the server (other than log out)

    // 5. Delete an account
    @Test
    public void createAccount_then_DeleteAccount_then_ListAllAccounts() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    // Undelivered messages are deleted along with user
    @Test
    public void createAccount__then_SendMessages_then_DeleteAccount_then_CreateAccount_then_LogIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    // Deleting the current account logs out the user
    @Test
    public void createAccount_then_LogIn_then_DeleteAccount_then_SendMessage() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    /* Wire diagram flow */

    // Ensure functionality of DeleteAccount
    @Test
    public void createAccount_then_DeleteAccount() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_DeleteAccount_then_CreateAccount() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_ListAccounts_then_DeleteAccount_then_ListAccounts() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    // Ensure functionality of LogIn
    @Test
    public void createAccount_then_LogIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccounts_then_LogIn_then_LogIn_then_SendMessage() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    // Ensure functionality of LogOut
    @Test
    public void createAccount_then_LogIn_then_LogOut() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    
    @Test
    public void createAccount_then_LogIn_then_SendMessage_then_LogOut() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    /* Other error checking */

    @Test
    public void deleteAccount_then_UserDoesNotExist() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_DeleteAccount_then_DeleteAccount_then_UserDoesNotExist() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }
    
    @Test
    public void logIn_then_UserDoesNotExist() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_DeleteAccount_then_LogIn_then_UserDoesNotExist() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void logOut_then_UserNotLoggedIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_logOut_then_UserNotLoggedIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_logIn_then_logOut_then_logOut_then_UserNotLoggedIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void sendMessage_then_UserNotLoggedIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    @Test
    public void createAccount_then_SendMessage_then_UserNotLoggedIn() {
        final String nameofCurrentMethod = new Throwable()
                                      .getStackTrace()[0]
                                      .getMethodName();
        runTest(nameofCurrentMethod);
    }

    /* Abstraction of test logic */

    public void runTest(String testName) {
        final String inputFile = folderPath + testName + "/in.txt";
        final String expectedOutputFile = folderPath + testName + "/out.txt";
        final String actualOutputFile = folderPath + outputFile;

        // change standard input to file
        InputStream fileIn = null;
        try {
            fileIn = new FileInputStream(new File(inputFile));
            System.setIn(fileIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        // change standard output to file
        PrintStream fileOut = null;
        try {
            fileOut = new PrintStream(actualOutputFile);
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        // run client
        try {
            Client.main(new String[0]);
        } catch (NoSuchElementException e) {
            // ignore when Scanner.in throws a NoSuchElementException because it does so when it reaches the end of the input, however this is simply because the input used for integration testing is not infinite, whereas the actual command line is
        }

        // Wait for 2 seconds to allow server to respond
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        // check if actual output has the same contents as expected output
        compareFiles(expectedOutputFile, actualOutputFile);

        // safely close streams
        try {
            fileIn.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    // Utility function comparing two files
    public static void compareFiles(String expected, String actual) {
        try {
            BufferedReader expectedReader = new BufferedReader(new FileReader(expected));
            BufferedReader actualReader = new BufferedReader(new FileReader(actual));

            String expectedLine = expectedReader.readLine();
            String actualLine = actualReader.readLine();

            while (expectedLine != null || actualLine != null) {
                // Ignore Server lines
                if (ignoreServerOutput) {
                    while (actualLine != null && actualLine.startsWith("[Server]")) {
                        actualLine = actualReader.readLine();
                    }
                }
    
                // Ignore stack trace
                if (ignoreStackTrace) {
                    while (actualLine != null && actualLine.trim().startsWith("at")) {
                        actualLine = actualReader.readLine();
                    }
                }

                if (expectedLine == null && actualLine == null) {
                    break;
                } else if (expectedLine == null && actualLine != null) {
                    assertTrue(false);
                } else if (expectedLine != null && actualLine == null) {
                    assertTrue(false);
                } else {
                    Assert.assertEquals(expectedLine, actualLine);
                }

                expectedLine = expectedReader.readLine();
                actualLine = actualReader.readLine();
            }

            expectedReader.close();
            actualReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
