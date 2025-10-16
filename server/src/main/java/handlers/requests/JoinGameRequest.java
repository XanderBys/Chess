package handlers.requests;

import chess.ChessGame;

public record JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameID) {
    public JoinGameRequest withAuthToken(String authToken) {
        return new JoinGameRequest(authToken, playerColor, gameID);
    }
}
