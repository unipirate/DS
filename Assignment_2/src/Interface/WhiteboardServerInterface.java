package Interface; /** WhiteboardServerInterface.java — 白板服务器远程接口定义 */
import Utility.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhiteboardServerInterface extends Remote {
    BoardState joinBoard(String username, WhiteboardClientInterface client) throws RemoteException, Exception;
    void leaveBoard(String username) throws RemoteException;
    void sendShape(String username, WhiteboardShape shape) throws RemoteException;
    void sendMessage(String username, String message) throws RemoteException;
    void kickUser(String adminName, String targetUsername) throws RemoteException;
    void closeBoard() throws RemoteException;
    void loadBoard(List<WhiteboardShape> shapes) throws RemoteException;
    void clearBoard() throws RemoteException;
}