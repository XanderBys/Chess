package handlers.requests;

public record CreateGameRequest(String authToken, String gameName) {
    public CreateGameRequest withAuthToken(String authToken) {
        return new CreateGameRequest(authToken, gameName);
    }
}
