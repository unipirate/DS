package Client; /** WhiteboardClient.java — 白板客户端程序实现 */
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

    public WhiteboardClient(String username) throws RemoteException {
        super();
        this.username = username;
    }

    // 启动客户端并连接服务器
    public void start(String host, int port) throws Exception{
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (WhiteboardServerInterface) registry.lookup("WhiteboardServer");
            BoardState state = server.joinBoard(username, this);
            this.isManager = state.isManager();
            this.gui = new WhiteboardGUI(username, server, this, state);
            server.sendMessage("系统", username + "加入了白板");
        } catch (Exception e) {
            System.err.println("连接失败: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "连接服务器失败: " + e.getMessage());
            throw e;
        }
    }

    // 被踢或服务器关闭后，重新进入登录界面
    public void reconnect() {
        try {
            UnicastRemoteObject.unexportObject(this, true); // 断开 RMI
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> WhiteboardLogin.showLogin());
    }

    public void shutdown() {
        try {
            UnicastRemoteObject.unexportObject(this, true); // 彻底解除 RMI 绑定
        } catch (Exception ignored) {}
    }

    // 客户端主入口
    public static void main(String[] args) {
        WhiteboardLogin.showLogin(); // 显示登录界面（需另写 WhiteboardLogin.java）
    }

    // ====== RMI 接口实现：被服务器远程调用 ======

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
    public void refreshChatHistory(List<String> history) throws RemoteException {
        gui.loadChatHistory(history);
    }

    @Override
    public void clearCanvas() throws RemoteException {
        gui.clearCanvas();
    }
}
