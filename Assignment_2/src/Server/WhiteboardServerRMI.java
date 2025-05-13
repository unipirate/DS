package Server;
import RMI.*;
import Utility.ShapeData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhiteboardServerRMI extends UnicastRemoteObject implements IWhiteboardService {
    private Map<String, ClientCallback> clients = new HashMap<>();
    private List<ShapeData> shapes = new ArrayList<>();

    public WhiteboardServerRMI() throws RemoteException {
        super();
    }

    @Override
    public boolean join(String username, ClientCallback callback) throws RemoteException {
        if (clients.containsKey(username)) {
            return false; // 用户名已存在
        }
        clients.put(username, callback);
        // 同步当前白板状态给新用户
        for (ShapeData shape : shapes) {
            callback.updateShape(shape);
        }
        return true;
    }

    @Override
    public void leave(String username) throws RemoteException {
        clients.remove(username);
    }

    @Override
    public void draw(ShapeData shape) throws RemoteException {
        shapes.add(shape);
        // 广播给所有客户端
        for (ClientCallback client : clients.values()) {
            client.updateShape(shape);
        }
    }

    @Override
    public void sendChatMessage(String username, String message) throws RemoteException {
        for (ClientCallback client : clients.values()) {
            client.updateChat(username, message);
        }
    }

    @Override
    public List<ShapeData> getWhiteboardState() throws RemoteException {
        return new ArrayList<>(shapes);
    }

    public static void main(String[] args) {
        try {
            WhiteboardServerRMI server = new WhiteboardServerRMI();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("WhiteboardService", server);
            System.out.println("Server ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}