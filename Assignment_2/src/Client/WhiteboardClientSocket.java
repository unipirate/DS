package Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Utility.Message;
import Utility.JSONUtils;
import org.json.simple.parser.ParseException;

public class WhiteboardClientSocket extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private JPanel canvas;
    private JTextArea chatArea = new JTextArea(5, 20);
    private JButton[] shapeButtons = new JButton[5];
    private Color currentColor = Color.BLACK;
    private int currentShape = 0; // 0:自由绘制, 1:直线, 2:矩形, 3:椭圆, 4:文本
    private String username;
    private List<ShapeData> permanentShapes = new ArrayList<>(); // 永久图形列表
    private ShapeData tempShape; // 临时图形（用于预览）
    private Point startPoint; // 鼠标按下时的起始坐标

    private class ShapeData {
        int type; // 图形类型
        int x1, y1, x2, y2; // 坐标
        Color color; // 颜色

        public ShapeData(int type, int x1, int y1, int x2, int y2, Color color) {
            this.type = type;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    public WhiteboardClientSocket(String serverIP, int port, String username) {
        this.username = username;
        try {
            socket = new Socket(serverIP, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            setupGUI();
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        processServerMessage(inputLine);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Connection lost!");
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server!");
        }
    }

    private void setupGUI() {
        setTitle("Shared Whiteboard - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 工具栏
        JToolBar toolbar = new JToolBar();
        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(_ -> currentColor = JColorChooser.showDialog(this, "Choose Color", currentColor));
        toolbar.add(colorBtn);

        // 图形选择按钮
        String[] shapeLabels = {"自由绘制", "直线", "矩形", "椭圆", "文本"};
        ButtonGroup shapeGroup = new ButtonGroup();
        for (int i = 0; i < shapeLabels.length; i++) {
            shapeButtons[i] = new JButton(shapeLabels[i]);
            shapeButtons[i].setFocusPainted(false);
            final int shapeType = i;

            // 设置选中状态样式
            shapeButtons[i].addActionListener(_ -> {
                currentShape = shapeType;
                updateButtonSelection(shapeType);
            });

            // 默认选中自由绘制
            if(i == 0) shapeButtons[i].setBackground(new Color(220, 220, 220));

            shapeGroup.add(shapeButtons[i]);
            toolbar.add(shapeButtons[i]);
        }

        // 画布
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 根据历史命令重绘（需实现）
                for (ShapeData shape : permanentShapes) {
                    drawShape(g, shape);
                }

                //临时绘图
                if(tempShape != null) {
                    drawShape(g, tempShape);
                }
            }

            private void drawShape(Graphics g, ShapeData shape) {
                g.setColor(shape.color);
                switch (shape.type) {
                    case 0: // 自由绘制（已直接发送，无需预览）
                        g.drawLine(shape.x1, shape.y1, shape.x2, shape.y2);
                        break;
                    case 1: // 直线
                        g.drawLine(shape.x1, shape.y1, shape.x2, shape.y2);
                        break;
                    case 2: // 矩形
                        int x = Math.min(shape.x1, shape.x2);
                        int y = Math.min(shape.y1, shape.y2);
                        int width = Math.abs(shape.x2 - shape.x1);
                        int height = Math.abs(shape.y2 - shape.y1);
                        g.drawRect(x, y, width, height);
                        break;
                    case 3: // 椭圆
                        x = Math.min(shape.x1, shape.x2);
                        y = Math.min(shape.y1, shape.y2);
                        width = Math.abs(shape.x2 - shape.x1);
                        height = Math.abs(shape.y2 - shape.y1);
                        g.drawOval(x, y, width, height);
                        break;
                }
            }
        };

        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                if (currentShape == 0) { // 自由绘制直接开始
                    tempShape = new ShapeData(0, e.getX(), e.getY(), e.getX(), e.getY(), currentColor);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (tempShape != null) {
                    // 添加永久图形并发送到服务器
                    permanentShapes.add(tempShape);
                    sendShapeToServer(tempShape);
                    tempShape = null;
                    repaint();
                }
                startPoint = null;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    if (currentShape == 0) { // 自由绘制
                        // 实时将线段添加到本地并发送
                        ShapeData segment = new ShapeData(
                                0,
                                startPoint.x, startPoint.y,
                                e.getX(), e.getY(),
                                currentColor
                        );
                        permanentShapes.add(segment); // 添加到本地永久列表
                        sendShapeToServer(segment);    // 发送到服务器
                        startPoint = e.getPoint();     // 更新起始点
                        repaint();                    // 立即重绘
                    } else {
                        // 其他图形显示预览
                        tempShape = new ShapeData(
                                currentShape,
                                startPoint.x, startPoint.y,
                                e.getX(), e.getY(),
                                currentColor
                        );
                        repaint();
                    }
                }
            }
        });

        // 聊天区域
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        JTextField chatInput = new JTextField();
        chatInput.addActionListener(e -> {
            sendChatMessage(chatInput.getText());
            chatInput.setText("");
        });
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        // 布局
        add(toolbar, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.SOUTH);

    }


    private void sendDrawCommand(int x1, int y1, int x2, int y2) {
        Message msg = new Message();
        msg.setType("DRAW");
        msg.setUsername(username);
        msg.setShape(currentShape);
        msg.setX1(x1);
        msg.setY1(y1);
        msg.setX2(x2);
        msg.setY2(y2);
        msg.setColor(currentColor.getRGB());
        out.println(JSONUtils.toJson(msg));
    }

    private void sendShapeToServer(ShapeData shape){
        Message msg = new Message();
        msg.setType("DRAW");
        msg.setUsername(username);
        msg.setShape(shape.type);
        msg.setX1(shape.x1);
        msg.setY1(shape.y1);
        msg.setX2(shape.x2);
        msg.setY2(shape.y2);
        msg.setColor(shape.color.getRGB());
        out.println(JSONUtils.toJson(msg));
    }

    private void sendChatMessage(String text) {
        Message msg = new Message();
        msg.setType("CHAT");
        msg.setUsername(username);
        msg.setMessage(text);
        out.println(JSONUtils.toJson(msg));
    }

    private void processServerMessage(String json) {
        try {
            Message msg = JSONUtils.fromJson(json);
            switch (msg.getType()) {
                case "DRAW":
                    SwingUtilities.invokeLater(() -> {
                        Graphics g = canvas.getGraphics();
                        g.setColor(new Color(msg.getColor()));
                        switch (msg.getShape()) {
                            case 0: // 自由绘制
                                g.drawLine(msg.getX1(), msg.getY1(), msg.getX2(), msg.getY2());
                                break;
                            case 1: // 直线
                                g.drawLine(msg.getX1(), msg.getY1(), msg.getX2(), msg.getY2());
                                break;
                            case 2: // 矩形
                                g.drawRect(Math.min(msg.getX1(), msg.getX2()), Math.min(msg.getY1(), msg.getY2()),
                                        Math.abs(msg.getX2() - msg.getX1()), Math.abs(msg.getY2() - msg.getY1()));
                                break;
                            case 3: // 椭圆
                                g.drawOval(Math.min(msg.getX1(), msg.getX2()), Math.min(msg.getY1(), msg.getY2()),
                                        Math.abs(msg.getX2() - msg.getX1()), Math.abs(msg.getY2() - msg.getY1()));
                                break;
                        }
                    });
                    break;
                case "CHAT":
                    chatArea.append(msg.getUsername() + ": " + msg.getMessage() + "\n");
                    break;
            }
        } catch (ParseException e) {
            System.err.println("JSON解析失败: " + json);
        }
    }

    private void updateButtonSelection(int selectedIndex) {
        for (int i = 0; i < shapeButtons.length; i++) {
            if(i == selectedIndex) shapeButtons[i].setBackground(new Color(220, 220, 220));
            else shapeButtons[i].setBackground(UIManager.getColor("Button.background"));
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Client <serverIP> <port> <username>");
            return;
        }
        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        new WhiteboardClientSocket(serverIP, port, username).setVisible(true);
    }
}