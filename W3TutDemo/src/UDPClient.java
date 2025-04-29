import java.io.BufferedReader; // Imports the BufferedReader class for reading text from the input stream
import java.io.IOException; // Imports the IOException class for handling input/output exceptions
import java.io.InputStreamReader; // Imports the InputStreamReader class for converting byte streams to character streams
import java.net.InetAddress; // Imports the InetAddress class for getting IP addresses
import java.net.SocketException; // Imports the SocketException class for handling socket-related exceptions
import java.net.DatagramPacket; // Imports the DatagramPacket class for creating data packets
import java.net.DatagramSocket; // Imports the DatagramSocket class for creating UDP sockets

public class UDPClient {

    public static void main(String args[]) {

        DatagramSocket clientSocket = null;

        try {

            System.out.println("This is UDP Client - Enter some text to send to the UDP server");
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); // Reader for user input

            // Create a UDP socket object
            clientSocket = new DatagramSocket();
            // IP and port for socket
            InetAddress IPAddress = InetAddress.getByName("localhost"); // Server's IP address
            int port = 9884; // Server's port number

            // As UDP Datagrams are bounded by fixed message boundaries, define the length
            byte[] sendData = new byte[1024]; // Byte array for sending data
            byte[] receiveData = new byte[1024]; // Byte array for receiving data

            String sentence = inFromUser.readLine(); // Read input from the user
            sendData = sentence.getBytes(); // Convert the input string to bytes

            // Create a send Datagram packet and send through socket
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket); // Send the packet

            // Create a receive Datagram packet and receive through socket
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket); // Receive the packet
            String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength()); // Convert received data to string
            System.out.println("This is client, SERVER SENT: " + modifiedSentence); // Print the received message
            // Close the Socket
            clientSocket.close(); // Close the client socket

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle socket-related exceptions

        } catch (IOException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle input/output exceptions

        } finally {
            if (clientSocket != null)
                clientSocket.close(); // Ensure the socket is closed
        }

    }
}
