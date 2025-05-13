package RMI;
import Utility.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IWhiteboardService extends Remote {
    // 客户端加入白板
    boolean join(String username, ClientCallback callback) throws RemoteException;
    // 客户端离开白板
    void leave(String username) throws RemoteException;
    // 发送绘图指令到服务器
    void draw(ShapeData shape) throws RemoteException;
    // 发送聊天消息
    void sendChatMessage(String username, String message) throws RemoteException;
    // 获取当前白板状态（所有图形）
    List<ShapeData> getWhiteboardState() throws RemoteException;
}
