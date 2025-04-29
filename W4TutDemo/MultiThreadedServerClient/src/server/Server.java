package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

/**
 * This code implements a simple multi-threaded TCP-based server that listens for client connections
 * on a specified port (port 3005). For each client connection, the server spawns a new thread
 * to handle communication, allowing multiple clients to connect and interact with the server
 * simultaneously.
 */

public class Server {

    // Declare the port number on which the server will listen for connections
    private static int port = 3005;

    // Counter to track the number of connected clients
    private static int counter = 0;

    public static void main(String[] args) {
        // Create a ServerSocketFactory instance to create the ServerSocket
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        // Try to create a ServerSocket on the specified port
        try(ServerSocket server = factory.createServerSocket(port)) {
            System.out.println("Waiting for client connection-");

            // Continuously wait for client connections
            while(true) {
                // Accept a client connection; returns a Socket object representing the client
                Socket client = server.accept();
                counter++; // Increment the counter for each new client connection
                System.out.println("Client " + counter + ": Applying for connection!");

                // Start a new thread to handle communication with the connected client (Thread-per-connection)
                Thread t = new Thread(() -> serveClient(client));
                t.start(); // Begin the execution of the thread
            }

        } catch (IOException e) {
            e.printStackTrace(); // Handle any IOExceptions that may occur
        }

    }

    // Method to handle communication with the client
    private static void serveClient(Socket client) {
        try(Socket clientSocket = client) {
            // Create input stream to receive data from the client
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Create output stream to send data to the client
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            // Read and print the message sent by the client
            System.out.println("CLIENT: " + input.readUTF());

            // Send a response message to the client
            output.writeUTF("Server: Hi Client " + counter + " !!!");
        } catch (IOException e) {
            e.printStackTrace(); // Handle any IOExceptions that may occur during communication
        }
    }

}
