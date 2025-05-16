package Client.Join;

import Client.WhiteboardClient;

public class JoinWhiteBoard {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java JoinWhiteBoard <serverIP> <port> <username>");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        try {
            WhiteboardClient client = new WhiteboardClient(username, false);
            client.start(ip, port);
        } catch (Exception e) {
            System.err.println("连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

