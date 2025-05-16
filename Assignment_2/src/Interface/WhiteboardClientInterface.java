package Interface;
import Utility.WhiteboardShape;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhiteboardClientInterface extends Remote {
    void receiveShape(WhiteboardShape shape) throws RemoteException;
    void refreshCanvas(List<WhiteboardShape> shapes) throws RemoteException;
    void receiveMessage(String username, String message) throws RemoteException;
    void updateUserList(List<String> userList) throws RemoteException;
    void serverNotification(String message) throws RemoteException;
    boolean confirmJoinRequest(String username) throws RemoteException;
    void clearCanvas() throws RemoteException;
}