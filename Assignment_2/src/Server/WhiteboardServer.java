package Server;

import Client.DrawCommand;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * WhiteboardServer：接受多个客户端连接，
 * 将某客户端发来的 DrawCommand 广播给其它所有客户端。
 */
public class WhiteboardServer {
    private final int port;
    private final List<ObjectOutputStream> clients = Collections.synchronizedList(new ArrayList<>());

    public WhiteboardServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        while (true) {
            Socket sock = serverSocket.accept();
            System.out.println("Client connected: " + sock.getRemoteSocketAddress());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            clients.add(oos);

            // 为每个客户端启动一个处理线程
            new Thread(() -> {
                try {
                    while (true) {
                        Object obj = ois.readObject();
                        if (obj instanceof DrawCommand) {
                            broadcast((DrawCommand) obj, oos);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Client disconnected.");
                } finally {
                    clients.remove(oos);
                }
            }).start();
        }
    }

    // 将 cmd 发给除了源 oos 外的所有客户端
    private void broadcast(DrawCommand cmd, ObjectOutputStream src) {
        synchronized (clients) {
            for (ObjectOutputStream oos : clients) {
                if (oos == src) continue;
                try {
                    oos.writeObject(cmd);
                    oos.flush();
                } catch (Exception e) {
                    // 忽略发送失败的客户端
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 12345;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        new WhiteboardServer(port).start();
    }
}
