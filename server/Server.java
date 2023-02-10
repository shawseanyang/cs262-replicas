package server;

import static server.ServerHandler.*;
import protocol.Constants;
import protocol.Marshaller;
import protocol.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
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

            try {
                // Create a new data input and output stream
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // Read the message
                byte[] message = dis.readAllBytes();

                // Unmarshall the message
                Message unmarshalledMessage = Marshaller.unmarshall(message);

                // Create a new thread to handle the message
                new Thread(new ServerHandler(unmarshalledMessage)).start();
            } catch (IOException e) {
                System.err.println("ERROR: Could not read the message.");
                e.printStackTrace();
                continue;
            }
        }
    }
}