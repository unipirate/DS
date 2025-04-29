import java.io.BufferedReader; // Imports the BufferedReader class for reading text from the input stream
import java.io.BufferedWriter; // Imports the BufferedWriter class for writing text to the output stream
import java.io.IOException; // Imports the IOException class for handling input/output exceptions
import java.io.InputStreamReader; // Imports the InputStreamReader class for converting byte streams to character streams
import java.io.OutputStreamWriter; // Imports the OutputStreamWriter class for converting character streams to byte streams
import java.net.ServerSocket; // Imports the ServerSocket class for creating server-side sockets
import java.net.Socket; // Imports the Socket class for creating client-side sockets
import java.net.SocketException; // Imports the SocketException class for handling socket-related exceptions

public class TCPServer {

    public static void main(String[] args) {

        ServerSocket listeningSocket = null;
        Socket clientSocket = null;

        try {
            // Create a server socket listening on port 4444
            listeningSocket = new ServerSocket(4444);
            int i = 0; // Counter to keep track of the number of clients

            // Listen for incoming connections indefinitely
            while (true) {
                System.out.println("Server listening on port 4444 for a connection");

                // Accept an incoming client connection request
                clientSocket = listeningSocket.accept(); // This method will block until a connection request is received
                i++;
                System.out.println("Client connection number " + i + " accepted:");
                // System.out.println("Remote Port: " + clientSocket.getPort());
                System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
                System.out.println("Local Port: " + clientSocket.getLocalPort());

                // Get the input/output streams for reading/writing data from/to the socket
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                // Read the message from the client and reply
                // Notice that no other connection can be accepted and processed until the last line of
                // code of this loop is executed, incoming connections have to wait until the current
                // one is processed unless...we use threads!
                String clientMsg = null;
                try {
                    while((clientMsg = in.readLine()) != null) {
                        System.out.println("Message from client " + i + ": " + clientMsg);
                        out.write("Server Ack " + clientMsg + "\n"); // Send acknowledgment to client
                        out.flush(); // Ensure data is sent
                        System.out.println("Response sent");
                    }
                    System.out.println("Server closed the client connection!!!!! - received null");
                } catch (SocketException e) {
                    System.out.println("Connection closed...");
                }

                // Close the client connection
                clientSocket.close();
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            System.out.println("An I/O error occurred");
            e.printStackTrace();
        } finally {
            if (listeningSocket != null) {
                try {
                    // Close the server socket
                    listeningSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
