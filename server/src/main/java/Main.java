public class Main {
    public static void main(String[] args) {
        Server chessServer = new Server();
        int port = chessServer.run(0);
        System.out.println("Chess server running on port " + port);
    }
}