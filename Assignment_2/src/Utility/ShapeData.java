package Utility;

import java.io.Serializable;
import java.awt.Color;

public class ShapeData implements Serializable {
    private int type;
    private int x1, y1, x2, y2, x3, y3;
    private Color color;

    public ShapeData(int type, int x1, int y1, int x2, int y2, Color color) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public ShapeData(int type, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.color = color;
    }

    // Getters（必须提供以支持RMI参数传递）
    public int getType() { return type; }
    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
    public int getX3() { return x2; }
    public int getY3() { return y2; }
    public Color getColor() { return color; }
}