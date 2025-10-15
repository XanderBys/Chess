import dataaccess.*;
import server.Server;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        try {
            UserDao userDao = new LocalUserDao();
            AuthTokenDao authDao = new LocalAuthTokenDao();
            GameDao gameDao = new LocalGameDao();

            UserService userService = new UserService(userDao, authDao);
            GameService gameService = new GameService(gameDao, authDao);
            ClearService clearService = new ClearService(userDao, authDao, gameDao);

            Server chessServer = new Server(userService, gameService, clearService);

            int port = chessServer.run(8080);
            System.out.println("Chess server running on port " + port);
        } catch (Exception e) {
            System.out.println("Unable to start server: " + e.getMessage());
        }
    }
}