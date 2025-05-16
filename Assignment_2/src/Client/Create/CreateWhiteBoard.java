package Client.Create;

import Client.WhiteboardClient;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java CreateWhiteBoard <serverIP> <port> <username>");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        try {
            WhiteboardClient client = new WhiteboardClient(username);
            client.start(ip, port);
        } catch (Exception e) {
            System.err.println("Fail to start the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
