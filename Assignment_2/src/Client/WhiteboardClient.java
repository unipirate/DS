package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * WhiteboardClient：GUI 窗口 + 网络通信
 */
public class WhiteboardClient extends JPanel {
    // 网络流
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    // 用于在 EDT 之外接收命令并在 EDT 中绘制
    private BlockingQueue<DrawCommand> recvQueue = new LinkedBlockingQueue<>();

    // 画布
    private BufferedImage canvas;
    private Point lastPoint;

    // 画笔属性
    private float strokeWidth = 2.0f;
    private Color penColor = Color.BLACK;

    public WhiteboardClient(String serverIp, int serverPort) throws Exception {
        // 初始化画布
        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        g.dispose();

        // 连接服务器
        Socket sock = new Socket(serverIp, serverPort);
        oos = new ObjectOutputStream(sock.getOutputStream());
        ois = new ObjectInputStream(sock.getInputStream());

        // 接收线程
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = ois.readObject();
                    if (obj instanceof DrawCommand) {
                        recvQueue.put((DrawCommand) obj);
                        SwingUtilities.invokeLater(this::processRecvQueue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 鼠标事件：绘制并发送
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                DrawCommand cmd = new DrawCommand(lastPoint, p, strokeWidth, penColor);
                // 本地绘制
                Graphics2D g2 = canvas.createGraphics();
                cmd.execute(g2);
                g2.dispose();
                repaint();
                lastPoint = p;
                // 发送给服务器
                try {
                    oos.writeObject(cmd);
                    oos.flush();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    /** 处理并绘制从网络收到的命令 */
    private void processRecvQueue() {
        DrawCommand cmd;
        while ((cmd = recvQueue.poll()) != null) {
            Graphics2D g2 = canvas.createGraphics();
            cmd.execute(g2);
            g2.dispose();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas,0,0,null);
    }

    /** 创建并显示窗口 */
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Distributed Whiteboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        // 简单的颜色选择按钮
        JPanel top = new JPanel();
        JButton black = new JButton("Black");
        black.addActionListener(e -> penColor = Color.BLACK);
        JButton red = new JButton("Red");
        red.addActionListener(e -> penColor = Color.RED);
        top.add(black); top.add(red);
        frame.add(top, BorderLayout.NORTH);

        frame.setSize(800,600);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java WhiteboardClient <serverIp> <serverPort>");
            System.exit(1);
        }
        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        WhiteboardClient panel = new WhiteboardClient(serverIp, serverPort);
        SwingUtilities.invokeLater(panel::createAndShowGUI);
    }
}
