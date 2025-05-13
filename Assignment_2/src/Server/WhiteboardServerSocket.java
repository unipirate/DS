package Server;

import java.net.*;
import java.io.*;
import java.util.*;
import Utility.JSONUtils;
import Utility.Message;
import org.json.simple.parser.ParseException;

public class WhiteboardServerSocket {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<String> drawingHistory = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                new Thread(client).start();
                // 发送历史操作给新客户端
                synchronized (drawingHistory) {
                    for (String cmd : drawingHistory) {
                        client.sendMessage(cmd);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String jsonMessage) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(jsonMessage);
            }
        }
        try {
            Message msg = JSONUtils.fromJson(jsonMessage);
            if ("DRAW".equals(msg.getType())) {
                drawingHistory.add(jsonMessage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                WhiteboardServerSocket.broadcast(inputLine);
            }
        } catch (IOException e) {
            WhiteboardServerSocket.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}