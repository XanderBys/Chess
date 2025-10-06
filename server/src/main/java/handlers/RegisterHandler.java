package handlers;

import io.javalin.http.Context;

public class RegisterHandler extends Handler {
    public static void handleRegister(Context ctx) {
        ctx.result("I'm a register handler! I don't do anything yet.\n");
    }
}