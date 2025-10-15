package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class LocalGameDao implements GameDao {
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int gameIdCounter = 1;

    @Override
    public int addGame(String gameName) {
        GameData gameData = new GameData(gameIdCounter, null, null,
                gameName, new ChessGame());

        games.put(gameIdCounter, gameData);

        gameIdCounter++;

        return gameData.gameID();
    }

    @Override
    public Collection<GameData> listCurrentGames() {
        return games.values();
    }

    @Override
    public GameData getGameDataById(int gameId) {
        return games.get(gameId);
    }

    @Override
    public void clear() {
        games = new HashMap<>();
    }
}
