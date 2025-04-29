import java.net.InetAddress; // Imports the InetAddress class for getting IP addresses
import java.net.DatagramPacket; // Imports the DatagramPacket class for creating data packets
import java.net.DatagramSocket; // Imports the DatagramSocket class for creating UDP sockets
import java.net.SocketException; // Imports the SocketException class for handling socket-related exceptions
import java.io.IOException; // Imports the IOException class for handling input/output exceptions

public class UDPServer {

    public static void main(String args[]) {
        DatagramSocket serverSocket = null;
        try {
            // Create a UDP server socket and bind it to port 9884
            serverSocket = new DatagramSocket(9884);
            byte[] receiveData = new byte[1024]; // Byte array for receiving data
            byte[] sendData = new byte[1024]; // Byte array for sending data

            // Listen for incoming connections indefinitely
            while (true) {
                System.out.println("This is UDP server - Waiting for data to receive");

                // Create a receive DatagramPacket and receive data through the socket
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket); // Receive the packet

                // Convert the received data to a string, considering the actual length of the received data
                String receiveSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String sendSentence = "This is Server, I received from client - ";
                sendSentence += receiveSentence; // Append the received sentence to the response
                System.out.println("Server Data: " + sendSentence);

                // Get client address and port from the received data
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String capitalizedSentence = sendSentence.toUpperCase(); // Convert the response to uppercase
                sendData = capitalizedSentence.getBytes(); // Convert the response to bytes

                // Create a send DatagramPacket and send data through the socket
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket); // Send the packet
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle socket-related exceptions
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage()); // Handle input/output exceptions
        } finally {
            if (serverSocket != null)
                serverSocket.close(); // Close the socket
        }
    }
}
