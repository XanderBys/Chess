package server;

public class ResponseException extends RuntimeException {
    private final int code;
    public ResponseException(int code, String message) {
        super("Error code " + code + ": " + message);
        this.code = code;
    }

    public int getErrorCode() {
        return code;
    }
}
