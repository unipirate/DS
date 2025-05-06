package server; // Defines the Java package as 'server'

import java.io.DataInputStream; // Imports the class for reading data from the input stream
import java.io.DataOutputStream; // Imports the class for writing data to the output stream
import java.io.File; // Imports the class for file handling
import java.io.IOException; // Imports the class for handling IO exceptions
import java.io.RandomAccessFile; // Imports the class for reading file content
import java.net.ServerSocket; // Imports the class for server socket handling
import java.net.Socket; // Imports the class for client socket handling
import java.util.Arrays; // Imports the class for handling arrays

import javax.net.ServerSocketFactory; // Imports the factory class for creating server sockets

import org.json.simple.JSONObject; // Imports the class for handling JSON objects
import org.json.simple.parser.JSONParser; // Imports the class for parsing JSON data
import org.json.simple.parser.ParseException; // Imports the class for handling JSON parsing exceptions

// Server class
public class W8Server {

    // Defines the port number for the server
    private static int port = 3003;

    // Identifies the number of connected clients
    private static int counter = 0;

    // Main method - entry point of the program
    public static void main(String[] args) {
        // Creates a factory object for creating server sockets
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        // Use try-with-resources to automatically close resources, create a server socket, and bind it to a port
        try(ServerSocket server = factory.createServerSocket(port)){
            System.out.println("Waiting for client connection.."); // Prints a message indicating the server is waiting for client connections

            // Continuously wait for connections
            while(true){
                Socket client = server.accept(); // Accepts a client connection and returns a new socket for client communication
                counter++; // Increments the client counter
                System.out.println("Client "+counter+": Applying for connection!"); // Prints the client number applying for connection

                // Starts a new thread for handling each client connection
                Thread t = new Thread(() -> serveClient(client)); // Creates a new thread and calls 'serveClient' method with the client socket
                t.start(); // Starts the new thread
            }

        } catch (IOException e) { // Handles IO exceptions
            e.printStackTrace(); // Prints stack trace for debugging
        }
    }

    // Method to handle each client connection
    private static void serveClient(Socket client){
        try(Socket clientSocket = client){ // Uses try-with-resources to automatically close the client socket when done

            // JSON parser for handling incoming JSON data
            JSONParser parser = new JSONParser();
            // Input stream for receiving data from the client
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output stream for sending data to the client
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("CLIENT: "+input.readUTF()); // Reads and prints the initial message from the client

            output.writeUTF("Server: Hi Client "+counter+" !!!"); // Sends a greeting message back to the client

            // Infinite loop to keep receiving data from the client
            while(true){
                if(input.available() > 0){ // Checks if there is data available to read
                    // Attempts to convert the received data to a JSON object
                    JSONObject command = (JSONObject) parser.parse(input.readUTF());
                    System.out.println("COMMAND RECEIVED: "+ command.toJSONString()); // Prints the received JSON command
                    parseCommand(command, output); // Calls 'parseCommand' method to handle the received command
                }
            }
        } catch (IOException | ParseException e) { // Catches IO and JSON parsing exceptions
            e.printStackTrace(); // Prints stack trace for debugging
        }
    }

    // Method to parse and execute the commands received from the client
    private static void  parseCommand(JSONObject command, DataOutputStream output) {

        // This section deals with the file handling
        if(command.get("command_name").equals("GET_FILE")){ // Checks if the command is 'GET_FILE'
            String fileName = (String) command.get("file_name"); // Retrieves the file name from the command
            // Checks if the specified file exists in the server's file directory
            File f = new File("server_files/"+fileName);
            if(f.exists()){
                // Sends a JSON object to the client to indicate that the file will be sent
                JSONObject trigger = new JSONObject();
                trigger.put("command_name", "SENDING_FILE"); // Sets command name as 'SENDING_FILE'
                trigger.put("file_name", fileName); // Sets the file name in the JSON object
                trigger.put("file_size", f.length()); // Sets the file size in the JSON object
                try {
                    // Sends the trigger JSON object to the client
                    output.writeUTF(trigger.toJSONString());

                    // Opens the file `f` in read-only mode using `RandomAccessFile` for sequential reading.
                    RandomAccessFile byteFile = new RandomAccessFile(f, "r");

                    // Creates a 1MB buffer to hold the file data before sending.
                    byte[] sendingBuffer = new byte[1024 * 1024];

                    // Variable `num` stores the number of bytes read from the file.
                    int num;

                    // Reads data from the file into `sendingBuffer` and stores the number of bytes read in `num`.
                    // Continues looping as long as `num` (number of bytes read) is greater than 0.
                    while ((num = byteFile.read(sendingBuffer)) > 0) {

                        // Prints the number of bytes being sent in this iteration.
                        System.out.println("value:" + num);

                        // Sends only the valid bytes from `sendingBuffer` (up to `num` bytes) to the client.
                        output.write(Arrays.copyOf(sendingBuffer, num));
                    }

                    // Closes the file after sending all the data.
                    byteFile.close();
                } catch (IOException e) { // Catches IO exceptions during file transfer
                    e.printStackTrace(); // Prints stack trace for debugging
                }
            }
            else{
                // Sends an error message or handle file not found case (currently not implemented)
            }
        }
    }
}
