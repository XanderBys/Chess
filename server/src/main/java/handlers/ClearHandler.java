package handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.ClearService;

public class ClearHandler extends ChessHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            clearService.clear();
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("server.Server error: " + e.getMessage());
        }
    }
}