package dataaccess;

import chess.ChessGame;

import java.util.HashSet;

public class LocalGameDao implements GameDao {
    private HashSet<ChessGame> games = new HashSet<>();

    @Override
    public void clear() {
        games = new HashSet<>();
    }
}
