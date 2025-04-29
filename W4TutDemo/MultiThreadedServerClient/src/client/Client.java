package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This code implements a simple TCP client that connects to a server on a specified IP address
 * and port (in this case, localhost at port 3005). The client sends a connection request message
 * to the server and waits for a response. Upon receiving the server's response, the client
 * prints the message and closes the connection.
 */

public class Client {

    // Server's IP address and port number
    private static String ip = "localhost";  // The IP address of the server, "localhost" means the same machine
    private static int port = 3005;          // The port number on which the server is listening

    public static void main(String[] args) {

        // Try to create a socket connection to the server at the specified IP and port
        try(Socket socket = new Socket(ip, port)) {
            // Create input and output streams for communication
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Message to send to the server
            String sendData = "I want to connect";

            // Send the message to the server
            output.writeUTF(sendData);
            System.out.println("Data sent to Server--> " + sendData);
            output.flush();  // Ensure all data is sent out

            // A flag to control the reading loop
            boolean flag = true;
            while(flag) {
                // Check if there is data available from the server
                if(input.available() > 0) {
                    // Read the message from the server
                    String message = input.readUTF();
                    System.out.println(message);
                    flag = false; // Exit the loop after receiving the message
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();  // Handle the case where the IP address is unknown
        } catch (IOException e) {
            e.printStackTrace();  // Handle I/O errors
        }

    }

}
