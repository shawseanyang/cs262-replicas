package server;

import protocol.Constants;
import protocol.Marshaller;
import protocol.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    // Inactive users should have a socket value of null
    static HashMap<User, Socket> clients = new HashMap<User, Socket>();
    public static void main(String[] args) {
        System.out.println("Server is running...");

        // Create a new server socket
        ServerSocket ss;
        try {
            ss = new ServerSocket(Constants.PORT);
        } catch (IOException e) {
            System.err.println("FATAL: Could not create a new server socket.");
            e.printStackTrace();
            return;
        }

        while (true) {
            // Accept a new connection
            Socket s;
            try {
                s = ss.accept();
            } catch (IOException e) {
                System.err.println("ERROR: Could not accept a new connection.");
                e.printStackTrace();
                continue;
            }

            // Read the message
            byte[] message;
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                message = dis.readAllBytes();
            } catch (IOException e) {
                System.err.println("ERROR: Could not read the message.");
                e.printStackTrace();
                continue;
            }

            // Unmarshall the message
            Message unmarshalledMessage = Marshaller.unmarshall(message);

            // Create a new data output stream
            DataOutputStream dos;
            try {
                dos = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                System.err.println("ERROR: Could not create a new data output stream.");
                e.printStackTrace();
                continue;
            }

            // Create a new thread to handle the message
            new Thread(new ServerHandler(unmarshalledMessage, s, dos)).start();
        }
    }
}