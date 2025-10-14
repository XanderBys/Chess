package dataaccess;

import chess.ChessGame;

import java.util.HashSet;

public class LocalGameDao extends GameDao {
    private static HashSet<ChessGame> games = new HashSet<>();

    @Override
    public void clear() {
        LocalGameDao.games = new HashSet<>();
    }
}
