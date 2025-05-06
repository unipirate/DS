package Client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

/**
 * 一个可序列化的绘图命令：从 p1 到 p2，指定颜色和笔宽
 */
public class DrawCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    public Point p1, p2;
    public float strokeWidth;
    public int rgbColor;

    public DrawCommand(Point p1, Point p2, float strokeWidth, Color color) {
        this.p1 = p1; this.p2 = p2;
        this.strokeWidth = strokeWidth;
        this.rgbColor = color.getRGB();
    }

    /** 在给定 Graphics2D 上执行绘制 */
    public void execute(Graphics2D g) {
        g.setColor(new Color(rgbColor));
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}
