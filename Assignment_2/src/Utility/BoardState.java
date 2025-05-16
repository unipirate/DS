package Utility;
import java.io.Serializable;
import java.util.List;

public class BoardState implements Serializable {
    private List<WhiteboardShape> shapes;
    private List<String> users;
    private boolean isManager;
    private List<String> chatMessages;

    public BoardState(List<WhiteboardShape> shapes, List<String> users, boolean isManager, List<String> chatMessages) {
        this.shapes = shapes;
        this.users = users;
        this.isManager = isManager;
        this.chatMessages = chatMessages;
    }

    public List<WhiteboardShape> getShapes() {
        return shapes;
    }

    public List<String> getUsers() {
        return users;
    }

    public boolean isManager() {
        return isManager;
    }

    public List<String> getChatMessages() {
        return chatMessages;
    }
}
