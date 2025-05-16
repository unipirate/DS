package Server;
import Interface.*;
import Utility.*;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class WhiteboardServer extends UnicastRemoteObject implements WhiteboardServerInterface {
    private Map<String, WhiteboardClientInterface> clients;
    private List<WhiteboardShape> shapes;
    private List<String> chatHistory;
    private String manager;

    public WhiteboardServer() throws RemoteException {
        clients = new HashMap<>();
        shapes = new ArrayList<>();
        chatHistory = new ArrayList<>();
        manager = null;
    }

    public synchronized BoardState joinBoard(String username, WhiteboardClientInterface client)
            throws Exception {
        if (clients.containsKey(username)) {
            throw new Exception("Name in Whiteboard already exists");
        }

        boolean isAdmin = false;
        if (manager == null) {
            manager = username;
            isAdmin = true;
        } else {
            boolean accepted = clients.get(manager).confirmJoinRequest(username);
            if (!accepted) throw new Exception("Manager has rejected join request");
        }

        clients.put(username, client);
        return new BoardState(shapes, new ArrayList<>(clients.keySet()), isAdmin, chatHistory);
    }

    public synchronized void leaveBoard(String username) throws RemoteException {
        WhiteboardClientInterface removed = clients.remove(username);
        if (removed != null) {
            System.out.println(username + " left the whiteboard");
        }
        if (username.equals(manager)) {
            broadcastShutdown("Manager has left the whiteboard, whiteboard has been removed");
            new Timer().schedule(new TimerTask() {
                public void run() {
                    System.exit(0);
                }
            }, 500);
        }
        broadcastUserList();
    }

    public synchronized void sendShape(String username, WhiteboardShape shape) throws RemoteException {
        shapes.add(shape);
        for (Map.Entry<String, WhiteboardClientInterface> entry : clients.entrySet()) {
            if (!entry.getKey().equals(username)) {
                entry.getValue().receiveShape(shape);
            }
        }
    }

    public synchronized void sendMessage(String username, String message) throws RemoteException {
        String full = username + ": " + message;
        chatHistory.add(full);
        for (WhiteboardClientInterface client : clients.values()) {
            client.receiveMessage(username, message);
        }
    }

    public synchronized void kickUser(String admin, String target) throws RemoteException {
        if (!admin.equals(manager) || !clients.containsKey(target)) return;
        clients.get(target).serverNotification("You are now kicked from the whiteboard");
        clients.remove(target);
        broadcastUserList();
    }

    public synchronized void closeBoard() throws RemoteException {
        broadcastShutdown("Manager has been closed, whiteboard has been removed");
        System.exit(0);
    }

    public synchronized void loadBoard(List<WhiteboardShape> newShapes) throws RemoteException {
        shapes = new ArrayList<>(newShapes);
        for (WhiteboardClientInterface client : clients.values()) {
            client.refreshCanvas(shapes);
        }
    }

    public synchronized void clearBoard() throws RemoteException {
        shapes.clear();
        for (WhiteboardClientInterface client : clients.values()) {
            client.clearCanvas();
        }
    }

    @Override
    public void notifyUserJoined(String username) throws RemoteException {
        broadcastUserList();
        sendMessage("System ", username + " has joined the whiteboard!");
    }

    private void broadcastShutdown(String message) {
        for (WhiteboardClientInterface client : clients.values()) {
            try {
                client.serverNotification(message);
            } catch (RemoteException ignored) {}
        }
    }

    private void broadcastUserList() throws RemoteException {
        List<String> users = new ArrayList<>(clients.keySet());
        for (WhiteboardClientInterface client : clients.values()) {
            client.updateUserList(users);
        }
    }

    public static void main(String[] args) {
        try {
            WhiteboardServer server = new WhiteboardServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("WhiteboardServer", server);
            System.out.println("Whiteboard Server Started，port: 1099");
        } catch (Exception e) {
            System.err.println("Server start failed: " + e.getMessage());
        }
    }
}
