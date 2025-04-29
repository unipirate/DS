//Author: FuQuan Gao
//StudentID: 1648979

import javax.swing.*;
import java.awt.*;

public class TCPServerUI extends JFrame {
    private final JTextField fileNameField;
    private final JTextField portField;
    private final JButton connectButton;
    private final JButton disconnectButton;
    private final JTextArea logArea;
    private boolean severStart = false;

    private UIServer server;

    public TCPServerUI(String porTNumber, String fileName) {
        setTitle("Java TCP Server UI");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row1.add(new JLabel("Name of the file will open:"), BorderLayout.WEST);
        fileNameField = new JTextField(fileName, 10);
        fileNameField.setHorizontalAlignment(JTextField.RIGHT);
        row1.add(fileNameField, BorderLayout.EAST);
        controlPanel.add(row1);


        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row2.add(new JLabel("Server Port Number:"), BorderLayout.WEST);
        portField = new JTextField(porTNumber, 8);
        portField.setHorizontalAlignment(JTextField.RIGHT);
        row2.add(portField, BorderLayout.EAST);
        controlPanel.add(row2);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row3.add(new JLabel("Message to the Client:"), BorderLayout.WEST);
        controlPanel.add(row3);

        JPanel row4 = new JPanel(new BorderLayout());
        row4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        connectButton = new JButton("Start Server.");
        disconnectButton = new JButton("Stop Server.");
        disconnectButton.setEnabled(false);
        row4.add(connectButton, BorderLayout.WEST);
        row4.add(disconnectButton, BorderLayout.EAST);
        controlPanel.add(row4);

        // 日志区域
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // 布局管理
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 事件处理
        connectButton.addActionListener(_ -> {
            severStart = true;
            String filename = fileNameField.getText();
            String port = portField.getText().trim();
            try {
                int portNumber = Integer.parseInt(port);
                // 这里可以加入端口接入逻辑，例如尝试建立连接
                appendLog("Trying to Starting the server, Port No: " + port + "...");
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);

                SwingWorker<Void, String> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        server = new UIServer(portNumber, TCPServerUI.this::appendLog, filename);
                        server.startServer();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            TCPServerUI.this.appendLog(e.getMessage());
                            connectButton.setEnabled(true);
                            disconnectButton.setEnabled(false);
                        }
                    }

                };
                worker.execute();
            } catch (Exception e) {
                TCPServerUI.this.appendLog("Port Error: Port has to be integer between 0 and 65535!");
            }

        });

        disconnectButton.addActionListener(_ -> {
            if (severStart) {
                // 模拟断开连接
                appendLog("Stopping Server...");
                severStart = false;
                server.stopServer();
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
            }
        });
    }

    // 日志打印方法
    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar DictionaryServer.jar <server-port>(int) <dictionary-file>");
            System.exit(1);
        }
        String portNumber = args[0];
        String filePath = args[1];
        SwingUtilities.invokeLater(() -> {
            TCPServerUI ui = new TCPServerUI(portNumber, filePath);
            ui.setVisible(true);
        });
    }
}
