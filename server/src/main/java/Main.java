import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            Server chessServer = new Server(true);

            int port = chessServer.run(8080);
            System.out.println("Chess server running on port " + port);
        } catch (Exception e) {
            System.out.println("Unable to start server: " + e.getMessage());
        }
    }
}