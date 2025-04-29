import java.io.BufferedReader; // Imports the BufferedReader class for reading text from the input stream
import java.io.BufferedWriter; // Imports the BufferedWriter class for writing text to the output stream
import java.io.IOException; // Imports the IOException class for handling input/output exceptions
import java.io.InputStreamReader; // Imports the InputStreamReader class for converting byte streams to character streams
import java.io.OutputStreamWriter; // Imports the OutputStreamWriter class for converting character streams to byte streams
import java.net.Socket; // Imports the Socket class for creating client-side sockets
import java.net.UnknownHostException; // Imports the UnknownHostException class for handling unknown host exceptions
import java.util.Scanner; // Imports the Scanner class for reading user input

public class TCPClient {

    public static void main(String[] args) {
        Socket socket = null;
        try {
            // Create a stream socket bounded to any port and connect it to the
            // socket bound to localhost on port 4444
            socket = new Socket("localhost", 8989); // Establish connection to server
            System.out.println("Connection established");

            // Get the input/output streams for reading/writing data from/to the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); // Input stream
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")); // Output stream

            Scanner scanner = new Scanner(System.in); // Scanner for user input
            String inputStr;

            // While the user input differs from "exit"
            while (!(inputStr = scanner.nextLine()).equalsIgnoreCase("exit")) {
                // Send the input string to the server by writing to the socket output stream
                out.write(inputStr + "\n"); // Write user input to output stream
                out.flush(); // Flush the output stream to ensure data is sent
                System.out.println("Message sent");
                // Receive the reply from the server by reading from the socket input stream
//                String received = in.readLine();
                String received;
                while ((received = in.readLine()) != null) { // Read server response
                    if(received.equalsIgnoreCase("END")) { break;}
                    System.out.println("Message received: " + received);// Print server response
                }
            }

            scanner.close(); // Close the scanner

        } catch (UnknownHostException e) {
            e.printStackTrace(); // Handle unknown host exception
        } catch (IOException e) {
            e.printStackTrace(); // Handle input/output exception
        } finally {
            // Close the socket
            if (socket != null) {
                try {
                    socket.close(); // Ensure the socket is closed
                } catch (IOException e) {
                    e.printStackTrace(); // Handle exception during socket close
                }
            }
        }
    }
}
