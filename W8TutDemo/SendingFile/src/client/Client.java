package client; // Defines the Java package as 'client'

import java.io.DataInputStream; // Imports the class for reading data from the input stream
import java.io.DataOutputStream; // Imports the class for writing data to the output stream
import java.io.IOException; // Imports the class for handling IO exceptions
import java.io.RandomAccessFile; // Imports the class for reading and writing files using random access
import java.net.Socket; // Imports the class for socket communication
import java.net.UnknownHostException; // Imports the class for handling unknown host exceptions
import java.util.Arrays; // Imports the class for handling arrays

import org.json.simple.JSONObject; // Imports the class for handling JSON objects
import org.json.simple.parser.JSONParser; // Imports the class for parsing JSON data
import org.json.simple.parser.ParseException; // Imports the class for handling JSON parsing exceptions

// Client class
public class Client {

    // IP address and port of the server
    private static String ip = "localhost"; // IP address of the server (local machine)
    private static int port = 3003; // Port number of the server

    public static void main(String[] args) {
        // Attempts to create a socket and connect to the server using the specified IP and port
        try(Socket socket = new Socket(ip, port);){
            // Initializes the input and output streams for the socket connection
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Sends an initial connection message to the server
            output.writeUTF("I want to connect!");
            output.flush(); // Flushes the output stream to ensure data is sent

            // Creates a JSON object representing the command to get a file
            JSONObject newCommand = new JSONObject();
            newCommand.put("command_name", "GET_FILE"); // Adds a key-value pair for the command name
            newCommand.put("file_name", "sauron.jpg"); // Specifies the file name to be requested

            System.out.println(newCommand.toJSONString()); // Prints the JSON command to the console

            // Reads the greeting message from the server
            String message = input.readUTF();
            System.out.println(message); // Prints the message from the server

            // Sends the file request command to the server
            output.writeUTF(newCommand.toJSONString());
            output.flush(); // Flushes the output stream again

            // Initializes a JSON parser for handling server responses
            JSONParser parser = new JSONParser();

            // Continuously listens for messages from the server
            while(true){
                if(input.available() > 0){ // Checks if there is data available to read

                    // Reads the server's response as a string
                    String result = input.readUTF();
                    System.out.println("Received from server: "+result); // Prints the received data

                    // Parses the received string into a JSON object
                    JSONObject command = (JSONObject) parser.parse(result);

                    // Checks if the JSON object contains a "command_name" key
                    if(command.containsKey("command_name")){

                        // If the command is "SENDING_FILE", it means the server is sending a file
                        if(command.get("command_name").equals("SENDING_FILE")){

                            // Retrieves the file name and prepares to save it in the client's directory
                            String fileName = "client_files/"+command.get("file_name");

                            // Creates a RandomAccessFile object to read and write the received file
                            RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");

                            // Retrieves the remaining file size from the server's response
                            long fileSizeRemaining = (Long) command.get("file_size");

                            // Determines the appropriate chunk size for downloading based on remaining file size
                            int chunkSize = setChunkSize(fileSizeRemaining);

                            // Creates a buffer array for receiving file data
                            byte[] receiveBuffer = new byte[chunkSize];

                            // Variable to store the number of bytes read
                            int num;

                            System.out.println("Downloading "+fileName+" of size "+fileSizeRemaining); // Prints download details

                            // Continuously reads and writes file data until the entire file is downloaded
                            while((num=input.read(receiveBuffer))>0){
                                // Writes the received bytes into the RandomAccessFile
                                downloadingFile.write(Arrays.copyOf(receiveBuffer, num));

                                // Reduces the remaining file size by the number of bytes received
                                fileSizeRemaining-=num;

                                // Adjusts the chunk size for the next read based on remaining file size
                                chunkSize = setChunkSize(fileSizeRemaining);
                                receiveBuffer = new byte[chunkSize];

                                // If there is no remaining file size to read, break the loop
                                if(fileSizeRemaining==0){
                                    break;
                                }
                            }
                            System.out.println("File received!"); // Prints confirmation that the file has been received
                            downloadingFile.close(); // Closes the RandomAccessFile
                        }
                    }
                }
            }

        } catch (UnknownHostException e) { // Catches and handles unknown host exceptions
            e.printStackTrace(); // Prints the stack trace for debugging
        } catch (IOException e) { // Catches and handles IO exceptions
            e.printStackTrace(); // Prints the stack trace for debugging
        } catch (ParseException e) { // Catches and handles JSON parsing exceptions
            e.printStackTrace(); // Prints the stack trace for debugging
        }
    }

    // Method to determine the appropriate chunk size for downloading based on remaining file size
    public static int setChunkSize(long fileSizeRemaining){
        // Default chunk size is 1MB
        int chunkSize=1024*1024;

        // If the remaining file size is less than the default chunk size, set chunk size to the remaining file size
        if(fileSizeRemaining<chunkSize){
            chunkSize=(int) fileSizeRemaining;
        }

        // Return the calculated chunk size
        return chunkSize;
    }
}
