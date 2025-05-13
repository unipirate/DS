package Utility;

public class Message {
    private String type;
    private String username;
    private int shape;
    private int x1, y1, x2, y2;
    private int color;
    private String message;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getShape() { return shape; }
    public void setShape(int shape) { this.shape = shape; }
    public int getX1() { return x1; }
    public void setX1(int x1) { this.x1 = x1; }
    public int getY1() { return y1; }
    public void setY1(int y1) { this.y1 = y1; }
    public int getY2() { return y2; }
    public void setY2(int y2) { this.y2 = y2; }
    public int getX2() { return x2; }
    public void setX2(int x2) { this.x2 = x2; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}