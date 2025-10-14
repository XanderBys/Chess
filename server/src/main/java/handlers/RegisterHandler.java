package handlers;

import io.javalin.http.Context;
import service.UserService;

public class RegisterHandler extends Handler {
    public static void handleRegister(Context ctx) {
        UserService userService = new UserService();

    }
}