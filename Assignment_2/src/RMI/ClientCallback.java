package RMI;

import Utility.ShapeData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    // 服务器向客户端推送新图形
    void updateShape(ShapeData shape) throws RemoteException;
    // 服务器向客户端推送聊天消息
    void updateChat(String username, String message) throws RemoteException;
    // 服务器强制客户端退出
    void forceExit(String reason) throws RemoteException;
}