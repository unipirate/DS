package Client;
import GUI.*;
import Interface.*;
import Utility.*;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.List;

public class WhiteboardClient extends UnicastRemoteObject implements WhiteboardClientInterface {
    private WhiteboardServerInterface server;
    private String username;
    private boolean isManager;
    private WhiteboardGUI gui;
    private boolean isCreator;

    public WhiteboardClient(String username, boolean isCreator) throws RemoteException {
        super();
        this.username = username;
    }


    public void start(String host, int port) throws Exception{
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (WhiteboardServerInterface) registry.lookup("WhiteboardServer");
            BoardState state = server.joinBoard(username, this, isCreator);
            this.isManager = state.isManager();
            this.gui = new WhiteboardGUI(username, server, this, state);
            gui.loadChatHistory(state.getChatMessages());
            server.notifyUserJoined(username);
        } catch (Exception e) {
            System.err.println("Connection fail: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void reconnect() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> WhiteboardLogin.showLogin());
    }

    public void shutdown() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception ignored) {}
    }


    public static void main(String[] args) {
        WhiteboardLogin.showLogin();
    }


    @Override
    public void receiveShape(WhiteboardShape shape) throws RemoteException {
        gui.addShape(shape);
    }

    @Override
    public void refreshCanvas(List<WhiteboardShape> shapes) throws RemoteException {
        gui.setShapes(shapes);
    }

    @Override
    public void receiveMessage(String from, String msg) throws RemoteException {
        gui.appendChat(from + ": " + msg);
    }

    @Override
    public void updateUserList(List<String> users) throws RemoteException {
        gui.setUserList(users);
    }

    @Override
    public void serverNotification(String msg) throws RemoteException {
        gui.showMessageAndReturnToLogin(msg);
    }

    @Override
    public boolean confirmJoinRequest(String username) throws RemoteException {
        return gui.confirmJoin(username);
    }

    @Override
    public void clearCanvas() throws RemoteException {
        gui.clearCanvas();
    }
}
