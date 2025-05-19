//Author: FuQuan Gao
//Student ID: 1648979
package GUI;

import Client.*;
import Interface.*;
import Utility.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

public class WhiteboardGUI {
    private JFrame frame;
    private DrawingCanvas canvas;
    private JTextArea chatArea;
    private JTextField inputField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JComboBox<String> penSizeCombo, eraserSizeCombo;
    private WhiteboardServerInterface server;
    private WhiteboardClient client;
    private String username;
    private boolean isManager;
    private List<WhiteboardShape> shapeList;
    private String currentTool = "free";
    private Color currentColor = Color.BLACK;
    private int currentStroke = 2;
    private boolean isDirty = false;

    public WhiteboardGUI(String username, WhiteboardServerInterface server,
                         WhiteboardClient client, BoardState state) {
        this.server = server;
        this.client = client;
        this.username = username;
        this.isManager = state.isManager();
        this.shapeList = new ArrayList<>(state.getShapes());

        initUI(state.getUsers());
    }
    private void initUI(List<String> users) {
        frame = new JFrame("Shared Whiteboard - " + username + (isManager ? "（Manager）" : ""));
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                SwingUtilities.invokeLater(() -> {
                    int res = JOptionPane.showConfirmDialog(
                            frame,
                            "Confirm to Exit?",
                            "Exit Whiteboard",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (res == JOptionPane.YES_OPTION) {
                        try {
                            if (isManager) {
                                new Thread(() -> {
                                    try {
                                        server.closeBoard();
                                    } catch (Exception ignored) {
                                    }
                                }).start();
                                client.shutdown();
                                frame.dispose();
                                System.exit(0);
                            } else {
                                server.leaveBoard(username);
                                client.shutdown();
                                frame.dispose();
                                client.reconnect();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

        // 画布
        canvas = new DrawingCanvas();
        frame.add(canvas, BorderLayout.CENTER);

        // 聊天区域
        chatArea = new JTextArea();
        JScrollPane chatScroll = new JScrollPane(chatArea);
        inputField = new JTextField();
        JButton sendBtn = new JButton("send");
        sendBtn.addActionListener(e -> sendChat());

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        JPanel chatBottom = new JPanel(new BorderLayout());
        chatBottom.add(inputField, BorderLayout.CENTER);
        chatBottom.add(sendBtn, BorderLayout.EAST);
        chatPanel.add(chatBottom, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(300, 150));
        frame.add(chatPanel, BorderLayout.SOUTH);
        // 工具栏
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        String[] tools = {"line", "rect", "oval", "triangle", "free", "eraser", "text"};
        for (String tool : tools) {
            JButton btn = new JButton(tool);
            btn.addActionListener(e -> {
                currentTool = tool;
                updateStrokeCombo();
            });
            leftPanel.add(btn);
        }

        penSizeCombo = new JComboBox<>(new String[]{"1", "2", "3"});
        penSizeCombo.addActionListener(e -> currentStroke = penSizeCombo.getSelectedIndex() * 2 + 1);
        penSizeCombo.setSelectedIndex(1);

        eraserSizeCombo = new JComboBox<>(new String[]{"1", "2", "3"});
        eraserSizeCombo.addActionListener(e -> currentStroke = eraserSizeCombo.getSelectedIndex() * 4 + 4);
        eraserSizeCombo.setSelectedIndex(1);

        leftPanel.add(new JLabel("Brush Size:"));
        leftPanel.add(penSizeCombo);
        leftPanel.add(new JLabel("Rubber Sizes:"));
        leftPanel.add(eraserSizeCombo);

        JPanel colorPanel = new JPanel(new GridLayout(2, 8, 2, 2));
        Color[] colors = {
                Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.PINK,
                Color.DARK_GRAY, Color.LIGHT_GRAY, Color.WHITE,
                new Color(128, 0, 128),
                new Color(0, 128, 128),
                new Color(139, 69, 19)
        };

        for (Color c : colors) {
            JButton b = new JButton();
            b.setBackground(c);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(24, 24));
            b.addActionListener(e -> currentColor = c);
            colorPanel.add(b);
        }

        leftPanel.add(new JLabel("Chosen Color:"));
        leftPanel.add(colorPanel);


        frame.add(leftPanel, BorderLayout.WEST);
        // 用户列表
        userListModel = new DefaultListModel<>();
        users.forEach(userListModel::addElement);
        userList = new JList<>(userListModel);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));
        frame.add(userScroll, BorderLayout.EAST);

        if (isManager) {
            JButton kickBtn = new JButton("Kick selected the user");
            kickBtn.addActionListener(e -> {
                String selected = userList.getSelectedValue();
                if (selected != null && !selected.equals(username)) {
                    try {
                        server.kickUser(username, selected);
                    } catch (Exception ignored) {}
                }
            });
            leftPanel.add(kickBtn);
        }


        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newBoard = new JMenuItem("New Board");
        newBoard.addActionListener(e -> {
            if (!isManager) return;
            int res = JOptionPane.showConfirmDialog(frame, "Confirm to clean the whiteboard？", "New Whiteboard", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    server.clearBoard();
                    shapeList.clear();
                    canvas.repaint();
                    isDirty = false;
                } catch (Exception ignored) {}
            }
        });

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save to XML");

             fileChooser.setSelectedFile(new File("whiteboard.xml"));
             fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML file (*.xml)", "xml"));

            int userSelection = fileChooser.showSaveDialog(frame); // 使用 frame 作为父组件

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (!fileToSave.getAbsolutePath().endsWith(".xml")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
                }
                try {

                    WhiteboardXMLUtils.saveToXML(shapeList, fileToSave);
                    isDirty = false;
                    JOptionPane.showMessageDialog(frame, "Whiteboard has saved " + fileToSave.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Fail to save: " + ex.getMessage(), "Error Saving", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> {

            if (isDirty) {
                int res = JOptionPane.showConfirmDialog(frame, "There are unsaved changes on the current whiteboard. Continuing to open it will result in the loss of these changes.\nContinue？", "Open Whiteboard", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (res != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open XML file");

             fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML file (*.xml)", "xml"));

            int userSelection = fileChooser.showOpenDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();
                try {

                    List<WhiteboardShape> loadedShapes = WhiteboardXMLUtils.loadFromXML(fileToOpen);
                    if (loadedShapes != null) {

                        server.loadBoard(loadedShapes);

                        isDirty = false;
                        JOptionPane.showMessageDialog(frame, "Whiteboard has loaded from " + fileToOpen.getName());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Fail to Open file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        fileMenu.add(newBoard);
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        if (isManager) {
            frame.setJMenuBar(menuBar);
        }

        frame.setVisible(true);
    }
    private void sendChat() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            try {
                server.sendMessage(username, text);
            } catch (Exception ignored) {}
            inputField.setText("");
        }
    }

    private void updateStrokeCombo() {
        penSizeCombo.setEnabled(currentTool.equals("free"));
        eraserSizeCombo.setEnabled(currentTool.equals("eraser"));
    }

    public void addShape(WhiteboardShape s) {
        shapeList.add(s);
        canvas.repaint();
    }

    public void setShapes(List<WhiteboardShape> list) {
        shapeList = new ArrayList<>(list);
        canvas.repaint();
    }

    public void setUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            users.forEach(userListModel::addElement);
        });
    }

    public void appendChat(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void loadChatHistory(List<String> history) {
        chatArea.setText("");
        for (String s : history) {
            chatArea.append(s + "\n");
        }
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public boolean confirmJoin(String username) {
        int res = JOptionPane.showConfirmDialog(frame, username + " Require to entry", "Join request", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public void showMessageAndReturnToLogin(String msg) {
        SwingUtilities.invokeLater(() -> {
            if (frame != null && frame.isDisplayable()) {
                JOptionPane.showMessageDialog(frame, msg);
                frame.dispose();
            }
            try {
                client.shutdown();
            } catch (Exception ignored) {}

             client.reconnect();
        });
    }


    public void clearCanvas() {
        shapeList.clear();
        canvas.repaint();
    }

    public void dispose() {
        frame.dispose();
    }

    class DrawingCanvas extends JPanel {
        private Point start = null;
        private List<Point> currentFreePoints = new ArrayList<>();
        private Point currentDragPoint = null;

        public DrawingCanvas() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    start = e.getPoint();
                    currentDragPoint = null;

                    if (currentTool.equals("free") || currentTool.equals("eraser")) {
                        currentFreePoints = new ArrayList<>();
                        currentFreePoints.add(start);
                    } else if (currentTool.equals("text")) {
                        String input = JOptionPane.showInputDialog("Please enter a text to display:");
                        if (input != null) {
                            List<Point> pts = List.of(start);
                            WhiteboardShape s = new WhiteboardShape("text", pts, currentColor, 1, input);
                            shapeList.add(s);
                            repaint();
                            try {
                                server.sendShape(username, s);
                            } catch (RemoteException ignored) {}
                        }
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (start == null) return;
                    Point end = e.getPoint();
                    currentDragPoint = null;

                    if (currentTool.equals("free") || currentTool.equals("eraser")) {
                        Color drawColor = currentTool.equals("eraser") ? canvas.getBackground() : currentColor;
                        WhiteboardShape shape = new WhiteboardShape("free",
                                new ArrayList<>(currentFreePoints), drawColor, currentStroke);
                        shapeList.add(shape);
                        repaint();
                        try {
                            server.sendShape(username, shape);
                        } catch (RemoteException ignored) {}
                        currentFreePoints.clear();
                    } else {
                        List<Point> pts;
                        switch (currentTool) {
                            case "triangle": Point top = new Point((start.x + end.x) / 2, start.y);
                                Point left = new Point(start.x, end.y);
                                Point right = new Point(end.x, end.y);
                                pts = List.of(top, left, right); break;
                                default:
                                    pts = List.of(start, end);
                        }
                        WhiteboardShape shape = new WhiteboardShape(currentTool, pts, currentColor, currentStroke);
                        if (shape != null) {
                            shapeList.add(shape);
                            repaint();
                            try {
                                server.sendShape(username, shape);
                            } catch (RemoteException ignored) {}
                        }
                    }

                    start = null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if ((currentTool.equals("free") || currentTool.equals("eraser")) && currentFreePoints != null) {
                        currentFreePoints.add(e.getPoint());
                    } else {
                        currentDragPoint = e.getPoint();
                    }
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            for (WhiteboardShape s : shapeList) {
                g2.setColor(s.getColor());
                g2.setStroke(new BasicStroke(s.getStrokeWidth()));
                List<Point> pts = s.getPoints();
                switch (s.getType()) {
                    case "line":
                        g2.drawLine(pts.get(0).x, pts.get(0).y, pts.get(1).x, pts.get(1).y);
                        break;
                    case "rect":
                        int x = Math.min(pts.get(0).x, pts.get(1).x);
                        int y = Math.min(pts.get(0).y, pts.get(1).y);
                        int w = Math.abs(pts.get(0).x - pts.get(1).x);
                        int h = Math.abs(pts.get(0).y - pts.get(1).y);
                        g2.drawRect(x, y, w, h);
                        break;
                    case "oval":
                        x = Math.min(pts.get(0).x, pts.get(1).x);
                        y = Math.min(pts.get(0).y, pts.get(1).y);
                        w = Math.abs(pts.get(0).x - pts.get(1).x);
                        h = Math.abs(pts.get(0).y - pts.get(1).y);
                        g2.drawOval(x, y, w, h);
                        break;
                    case "triangle":
                        Polygon triangle = new Polygon();
                        for (Point p : pts) triangle.addPoint(p.x, p.y);
                        g2.drawPolygon(triangle);
                        break;
                    case "free":
                        for (int i = 1; i < pts.size(); i++) {
                            Point p1 = pts.get(i - 1);
                            Point p2 = pts.get(i);
                            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                        break;
                    case "text":
                        g2.drawString(s.getText(), pts.get(0).x, pts.get(0).y);
                        break;
                }
            }


            if ((currentTool.equals("free") || currentTool.equals("eraser")) && !currentFreePoints.isEmpty()) {
                g2.setColor(currentTool.equals("eraser") ? Color.WHITE : currentColor);
                g2.setStroke(new BasicStroke(currentStroke));
                for (int i = 1; i < currentFreePoints.size(); i++) {
                    Point p1 = currentFreePoints.get(i - 1);
                    Point p2 = currentFreePoints.get(i);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }



            if (start != null && currentDragPoint != null) {
                g2.setColor(currentColor);
                g2.setStroke(new BasicStroke(currentStroke));

                List<Point> pts = List.of(start, currentDragPoint);
                switch (currentTool) {
                    case "line":
                        g2.drawLine(start.x, start.y, currentDragPoint.x, currentDragPoint.y);
                        break;
                    case "rect":
                        int x = Math.min(start.x, currentDragPoint.x);
                        int y = Math.min(start.y, currentDragPoint.y);
                        int w = Math.abs(start.x - currentDragPoint.x);
                        int h = Math.abs(start.y - currentDragPoint.y);
                        g2.drawRect(x, y, w, h);
                        break;
                    case "oval":
                        x = Math.min(start.x, currentDragPoint.x);
                        y = Math.min(start.y, currentDragPoint.y);
                        w = Math.abs(start.x - currentDragPoint.x);
                        h = Math.abs(start.y - currentDragPoint.y);
                        g2.drawOval(x, y, w, h);
                        break;
                    case "triangle":
                        Polygon tri = new Polygon();
                        tri.addPoint((start.x + currentDragPoint.x) / 2, start.y);
                        tri.addPoint(start.x, currentDragPoint.y);
                        tri.addPoint(currentDragPoint.x, currentDragPoint.y);
                        g2.drawPolygon(tri);
                        break;
                }
            }
        }
    }
}
