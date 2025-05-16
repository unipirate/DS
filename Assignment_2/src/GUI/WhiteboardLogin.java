package GUI;

import Client.WhiteboardClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

public class WhiteboardLogin {
    public static void showLogin() {
        JFrame frame = new JFrame("Connect to Whiteboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(5, 1, 10, 5));

        JTextField ipField = new JTextField("127.0.0.1");
        JTextField portField = new JTextField("1099");
        JTextField nameField = new JTextField();

        frame.add(new JLabel("Server IP:"));
        frame.add(ipField);
        frame.add(new JLabel("Port:"));
        frame.add(portField);
        frame.add(new JLabel("Username:"));
        frame.add(nameField);

        JButton connectBtn = new JButton("Connect");
        connectBtn.addActionListener((ActionEvent e) -> {
            String ip = ipField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Port has to be numeric");
                return;
            }
            String username = nameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username is empty!");
                return;
            }

            try {
                WhiteboardClient client = new WhiteboardClient(username);
                client.start(ip, port);
                frame.dispose();  // 连接成功后关闭登录窗口
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(frame, "Client starting fail：" + ex.getMessage());
            } catch (Exception ex){
                JOptionPane.showMessageDialog(frame, "Server connection fail" + ex.getMessage());
            }
        });

        frame.add(connectBtn);
        frame.setVisible(true);
    }
}
