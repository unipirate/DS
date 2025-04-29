//Author: FuQuan Gao
//StudentID: 1648979

import java.io.BufferedReader; // Imports the BufferedReader class for reading text from the input stream
import java.io.BufferedWriter; // Imports the BufferedWriter class for writing text to the output stream
import java.io.IOException; // Imports the IOException class for handling input/output exceptions
import java.io.InputStreamReader; // Imports the InputStreamReader class for converting byte streams to character streams
import java.io.OutputStreamWriter; // Imports the OutputStreamWriter class for converting character streams to byte streams
import java.net.*;
import java.util.function.Consumer;

public class UIClient {

    private final int port;
    private volatile boolean serverRunning;
    private String ipAddress;
    private final Consumer<String> statusCallback;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public UIClient(String ipAddress, int port, Consumer<String> statusCallback) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.statusCallback = statusCallback;
    }

    public synchronized void stopClient() {
        serverRunning = false;
        closeResources();
        statusCallback.accept("Server stopped");
    }

    private void closeResources() {
        try {
            socket.close();
        } catch (IOException e) {
            statusCallback.accept("Error closing client socket: " + e.getMessage());
        }
    }

    public void startClient() throws Exception {
        try {
            // Create a stream socket bounded to any port and connect it to the
            // socket bound to localhost on port 4444
            socket = new Socket();

            socket.connect(
                    new InetSocketAddress(ipAddress, port), 3000);// Establish connection to server
            statusCallback.accept("Connection established");
            serverRunning = true;


            // Get the input/output streams for reading/writing data from/to the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); // Input stream
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")); // Output stream


            // Receive the reply from the server by reading from the socket input stream
            while (serverRunning) {
                String received;
                while ((received = in.readLine()) != null) { // Read server response
                    if (received.equalsIgnoreCase("END")) {
                        break;
                    }
                    statusCallback.accept(received);// Print server response
                }// Print server response
            }

        } catch (ConnectException e) {
            statusCallback.accept("Connection refused!!");
            throw e;
        } catch (SocketTimeoutException e) {
            statusCallback.accept("Connection Timeout!!");
            throw e;
        } catch (NoRouteToHostException e) {
            statusCallback.accept("No Route to Host!!");
            throw e;
        } catch (UnknownHostException e) {
            statusCallback.accept("UnKnow Host!!"); // Handle unknown host exception
            throw e;
        } catch (IOException e) {
            if(!serverRunning)
                statusCallback.accept("Unexpected IO Error!!"); // Handle input/output exception
            throw e;
        } finally {
            // Close the socket
            if (socket != null) {
                try {
                    socket.close();// Ensure the socket is closed
                    serverRunning = false;
                } catch (IOException e) {
                    statusCallback.accept(e.getMessage()); // Handle exception during socket close
                }
            }
        }
    }

    public void sendMessage(String message) {
        try {
                if(out != null) {
                    out.write(message + "\n");
                    out.flush();
                }
        } catch (IOException e) {
            statusCallback.accept(e.getMessage());
        }
    }
}
