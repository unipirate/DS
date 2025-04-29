//Author: FuQuan Gao
//StudentID: 1648979

import javax.swing.*;
import java.awt.*;

public class TCPClientUI extends JFrame {
    private final JTextField ipField;
    private final JTextField portField;
    private final JButton connectButton;
    private final JButton disconnectButton;
    private final JButton sendButton;
    private final JTextArea logArea;
    private final JTextField messageField;

    private UIClient client;


    public TCPClientUI(String serverIP, String porTNumber) {
        setTitle("Java TCP Client UI");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row1.add(new JLabel("Server Address:"), BorderLayout.WEST);
        ipField = new JTextField(serverIP, 10);
        ipField.setHorizontalAlignment(JTextField.RIGHT);
        row1.add(ipField, BorderLayout.EAST);
        controlPanel.add(row1);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row2.add(new JLabel("Port:"), BorderLayout.WEST);
        portField = new JTextField(porTNumber, 8);
        portField.setHorizontalAlignment(JTextField.RIGHT);
        row2.add(portField, BorderLayout.EAST);
        controlPanel.add(row2);

        JPanel row5 = new JPanel(new BorderLayout());
        row5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        connectButton = new JButton("Connect.");
        disconnectButton = new JButton("Disconnect.");
        disconnectButton.setEnabled(false);
        row5.add(connectButton, BorderLayout.WEST);
        row5.add(disconnectButton, BorderLayout.EAST);
        controlPanel.add(row5);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row3.add(new JLabel("Message to the Server:"), BorderLayout.WEST);
        controlPanel.add(row3);

        JPanel row4 = new JPanel(new BorderLayout());
        row4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messageField = new JTextField("Message to Server.");
        row4.add(messageField, BorderLayout.CENTER);
        controlPanel.add(row4);

        JPanel row6 = new JPanel(new BorderLayout());
        row6.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sendButton = new JButton("Send.");
        row6.add(sendButton, BorderLayout.CENTER);
        controlPanel.add(row6);
        sendButton.setEnabled(false);

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

            String ip = ipField.getText().trim();
            String port = portField.getText().trim();

            try {
                // 这里可以加入端口接入逻辑，例如尝试建立连接
                appendLog("Trying to connect to " + ip + ", Port No: " + port + "...");

                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                sendButton.setEnabled(true);

                SwingWorker<Void, String> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        client = new UIClient(ip, Integer.parseInt(port), TCPClientUI.this::appendLog);
                        client.startClient();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            sendButton.setEnabled(true);
                        } catch (Exception e) {
                            TCPClientUI.this.appendLog(e.getMessage());
                            connectButton.setEnabled(true);
                            disconnectButton.setEnabled(false);
                            sendButton.setEnabled(false);
                        }
                    }

                };
                worker.execute();

            } catch (Exception e) {
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                sendButton.setEnabled(false);
            }
        });

        disconnectButton.addActionListener(_ -> {
            appendLog("Disconnecting...");
            client.stopClient();
            appendLog("Disconnected.");
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendButton.setEnabled(false);

        });

        sendButton.addActionListener(_ -> {
            String message = messageField.getText().trim();

            if (!message.isEmpty()) {
                client.sendMessage(message);
            } else {
                appendLog("Please enter something, do not leave empty.");
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
            System.err.println("Usage: java -jar DictionaryServer.jar <server-address> <server-port>");
            System.exit(1);
        }
        String serverAddress = args[0];
        String port = args[1];
        SwingUtilities.invokeLater(() -> {
            TCPClientUI ui = new TCPClientUI(serverAddress, port);
            ui.setVisible(true);
        });
    }
}
