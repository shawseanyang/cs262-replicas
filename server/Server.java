package server;

import static server.ServerHandler.*;
import protocol.Constants;
import protocol.Marshaller;
import protocol.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server is running...");

        // Create a new server socket
        ServerSocket ss = new ServerSocket(Constants.PORT);

        while (true) {
            // Accept a new connection
            Socket s = ss.accept();

            // Create a new data input and output stream
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Read the message
            byte[] message = dis.readAllBytes();
            
            // Unmarshall the message
            Message unmarshalledMessage = Marshaller.unmarshall(message);

            // Create a new thread to handle the message
            new Thread(new ServerHandler(unmarshalledMessage)).start();
        }
    }
}