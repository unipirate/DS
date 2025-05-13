package Client;

import RMI.*;
import Utility.ShapeData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardClientRMI extends UnicastRemoteObject implements ClientCallback, ActionListener {
    // GUI 组件
    private JFrame frame;
    private JPanel canvas;
    private JTextArea chatArea;
    private JTextField chatInput;

    // 绘图状态
    private Color currentColor = Color.BLACK;
    private int currentShape = 0; // 0:自由绘制 1:直线 2:矩形 3:椭圆 4:三角
    private Point startPoint;
    private List<ShapeData> shapes = new java.util.ArrayList<>();
    private ShapeData tempShape;  // 新增：临时图形（用于预览）
    private BasicStroke previewStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.5f, new float[]{5}, 0);
    private List<Point> trianglePoints = new ArrayList<>(); // 存储三角形顶点

    // RMI 相关
    private IWhiteboardService server;
    private String username;
    private boolean isManager = false;

    public WhiteboardClientRMI(String serverIP, String username) throws RemoteException, Exception {
        this.username = username;
        initializeGUI();
        connectToServer(serverIP);
    }

    private void initializeGUI() {
        frame = new JFrame("Whiteboard Shared - " + username);
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 工具栏
        JToolBar toolbar = new JToolBar();
        addShapeButtons(toolbar);
        addColorButton(toolbar);
        frame.add(toolbar, BorderLayout.NORTH);

        // 画布
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                if (currentShape == 4 && !trianglePoints.isEmpty()) {
                    g2d.setColor(currentColor);
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    for (int i = 0; i < trianglePoints.size(); i++) {
                        Point p = trianglePoints.get(i);
                        g2d.fillOval(p.x - 3, p.y - 3, 6, 6);
                        if (i > 0) {
                            Point prev = trianglePoints.get(i - 1);
                            g2d.drawLine(prev.x, prev.y, p.x, p.y);
                        }
                    }

                    if (trianglePoints.size() == 2) {
                        Point mousePos = canvas.getMousePosition();
                        if (mousePos != null) {
                            g2d.drawLine(trianglePoints.get(1).x, trianglePoints.get(1).y, mousePos.x, mousePos.y);
                            g2d.drawLine(mousePos.x, mousePos.y, trianglePoints.get(0).x, trianglePoints.get(0).y);
                        }
                    }
                }
                synchronized (shapes) {
                    for (ShapeData shape : shapes) {
                        drawShape(g2d, shape, false);
                    }
                }

                if (tempShape != null) {
                    drawShape(g2d, tempShape, true);
                }
                g2d.dispose();
            }
        };
        canvas.setBackground(Color.WHITE);
        setupCanvasListeners();
        frame.add(new JScrollPane(canvas), BorderLayout.CENTER);

        // 聊天区
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        chatInput = new JTextField();
        chatInput.addActionListener(this);
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        frame.add(chatPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void addShapeButtons(JToolBar toolbar) {
        String[] shapes = {"Freehand Drawing", "Line", "Rectangle", "Oval", "Triangle"};
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < shapes.length; i++) {
            JToggleButton btn = new JToggleButton(shapes[i]);
            btn.setActionCommand(String.valueOf(i));
            btn.addActionListener(_ -> {
                currentShape = Integer.parseInt(btn.getActionCommand());
                if (currentShape == 0) {
                    tempShape = null;
                    trianglePoints.clear();
                }
            });
            if (i == 0) btn.setSelected(true);
            group.add(btn);
            toolbar.add(btn);
        }
    }

    private void addColorButton(JToolBar toolbar) {
        JButton colorBtn = new JButton("颜色");
        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(frame, "选择颜色", currentColor);
            if (newColor != null) currentColor = newColor;
        });
        toolbar.add(colorBtn);
    }

    private void setupCanvasListeners() {
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (currentShape == 4) { // 三角形模式
                    trianglePoints.add(e.getPoint());
                    if (trianglePoints.size() == 3) { // 收集完三个顶点
                        // 创建三角形图形
                        Point p1 = trianglePoints.get(0);
                        Point p2 = trianglePoints.get(1);
                        Point p3 = trianglePoints.get(2);
                        ShapeData triangle = new ShapeData(4, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, currentColor);
                        sendShapeToServer(triangle);
                        trianglePoints.clear();
                        canvas.repaint();
                    }
                } else if (currentShape != 0) {  // 非自由绘制模式
                    startPoint = e.getPoint();
                    tempShape = new ShapeData(currentShape, e.getX(), e.getY(), e.getX(),  // 初始结束坐标同起点
                            e.getY(), new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 100)  // 半透明预览颜色
                    );
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (tempShape != null) {
                    // 创建最终图形（不透明）
                    ShapeData finalShape = new ShapeData(currentShape, tempShape.getX1(), tempShape.getY1(), e.getX(), e.getY(), currentColor);
                    sendShapeToServer(finalShape);
                    tempShape = null;
                    canvas.repaint();
                }
                startPoint = null;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (currentShape == 0 && startPoint != null) {
                    ShapeData line = new ShapeData(0, startPoint.x, startPoint.y, e.getX(), e.getY(), currentColor);
                    sendShapeToServer(line);
                    startPoint = e.getPoint();
                } else if (tempShape != null) {
                    // 更新临时图形结束坐标
                    tempShape = new ShapeData(currentShape, tempShape.getX1(), tempShape.getY1(), e.getX(), e.getY(), tempShape.getColor());
                    canvas.repaint();
                }
            }
        });
    }


    private void connectToServer(String serverIP) throws Exception {
        server = (IWhiteboardService) Naming.lookup("rmi://" + serverIP + "/WhiteboardService");
        if (!server.join(username, this)) {
            throw new Exception("User name already exists!");
        }
        shapes.addAll(server.getWhiteboardState());
        canvas.repaint();
    }

    private void drawShape(Graphics2D g2d, ShapeData shape, boolean isPreview) {
        g2d.setColor(shape.getColor());

        if (isPreview) {
            g2d.setStroke(previewStroke);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .5f));
        } else {
            g2d.setStroke(new BasicStroke(2));
        }

        switch (shape.getType()) {
            case 0: // 自由绘制
                g2d.drawLine(shape.getX1(), shape.getY1(), shape.getX2(), shape.getY2());
                break;
            case 1: // 直线
                g2d.drawLine(shape.getX1(), shape.getY1(), shape.getX2(), shape.getY2());
                break;
            case 2: // 矩形
                g2d.drawRect(Math.min(shape.getX1(), shape.getX2()), Math.min(shape.getY1(), shape.getY2()), Math.abs(shape.getX2() - shape.getX1()), Math.abs(shape.getY2() - shape.getY1()));
                break;
            case 3: // 椭圆
                g2d.drawOval(Math.min(shape.getX1(), shape.getX2()), Math.min(shape.getY1(), shape.getY2()), Math.abs(shape.getX2() - shape.getX1()), Math.abs(shape.getY2() - shape.getY1()));
                break;
            case 4: // 三角
                int[] xPoints = {shape.getX1(), shape.getX2(), shape.getX3()};
                int[] yPoints = {shape.getY1(), shape.getY2(), shape.getY3()};
                g2d.drawPolygon(xPoints, yPoints, 3);
                break;
        }
    }

    // 实现 ClientCallback 接口
    @Override
    public void updateShape(ShapeData shape) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            synchronized (shapes) {
                shapes.add(shape);
            }
            canvas.repaint();
        });
    }

    @Override
    public void updateChat(String username, String message) throws RemoteException {
        chatArea.append(username + ": " + message + "\n");
    }

    @Override
    public void forceExit(String reason) throws RemoteException {
        JOptionPane.showMessageDialog(frame, "您已被踢出: " + reason);
        System.exit(0);
    }

    // 事件处理
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chatInput) {
            String message = chatInput.getText().trim();
            if (!message.isEmpty()) {
                try {
                    server.sendChatMessage(username, message);
                    chatInput.setText("");
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(frame, "消息发送失败!");
                }
            }
        }
    }

    private void sendShapeToServer(ShapeData shape) {
        try {
            server.draw(shape);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(frame, "服务器连接失败!");
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: java Client <服务器IP> <用户名>");
            return;
        }
        try {
            new WhiteboardClientRMI(args[0], args[1]);
        } catch (Exception e) {
            System.err.println("客户端错误: " + e.getMessage());
        }
    }
}
