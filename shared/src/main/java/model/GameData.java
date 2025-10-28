package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData addUser(ChessGame.TeamColor color, String username) {
        if (color == ChessGame.TeamColor.WHITE) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        }
    }
}
