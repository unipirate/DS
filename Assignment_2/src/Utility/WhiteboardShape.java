package Utility;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardShape implements Serializable {
    private String type;
    private List<Point> points;
    private Color color;
    private int strokeWidth;
    private String text;

    public WhiteboardShape(String type, List<Point> points, Color color, int strokeWidth) {
        this(type, points, color, strokeWidth, null);
    }

    public WhiteboardShape(String type, List<Point> points, Color color, int strokeWidth, String text) {
        this.type = type;
        this.points = new ArrayList<>(points);
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.text = text;
    }

    public String getType() { return type; }
    public List<Point> getPoints() { return points; }
    public Color getColor() { return color; }
    public int getStrokeWidth() { return strokeWidth; }
    public String getText() { return text; }

    public void addPoint(Point p) { points.add(p); }
}