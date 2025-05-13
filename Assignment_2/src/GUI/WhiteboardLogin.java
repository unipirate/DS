package GUI;

import Client.WhiteboardClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

public class WhiteboardLogin {
    public static void showLogin() {
        JFrame frame = new JFrame("连接共享白板");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(5, 1, 10, 5));

        JTextField ipField = new JTextField("127.0.0.1");
        JTextField portField = new JTextField("1099");
        JTextField nameField = new JTextField();

        frame.add(new JLabel("服务器 IP:"));
        frame.add(ipField);
        frame.add(new JLabel("端口:"));
        frame.add(portField);
        frame.add(new JLabel("用户名:"));
        frame.add(nameField);

        JButton connectBtn = new JButton("连接");
        connectBtn.addActionListener((ActionEvent e) -> {
            String ip = ipField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "端口必须是数字");
                return;
            }
            String username = nameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "用户名不能为空");
                return;
            }

            try {
                WhiteboardClient client = new WhiteboardClient(username);
                client.start(ip, port);
                frame.dispose();  // 连接成功后关闭登录窗口
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(frame, "启动客户端失败：" + ex.getMessage());
            } catch (Exception ex){
                JOptionPane.showMessageDialog(frame, "连接服务器失败" + ex.getMessage());
            }
        });

        frame.add(connectBtn);
        frame.setVisible(true);
    }
}
