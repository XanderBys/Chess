package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData addUser(ChessGame.TeamColor color, String username) {
        if (color == ChessGame.TeamColor.WHITE) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else if (color == ChessGame.TeamColor.BLACK) {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        } else {
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        }
    }

    public GameData removeUser(String username) {
        if (username.equals(whiteUsername)) {
            return new GameData(gameID, null, blackUsername, gameName, game);
        } else if (username.equals(blackUsername)) {
            return new GameData(gameID, whiteUsername, null, gameName, game);
        } else {
            return this;
        }
    }
}
