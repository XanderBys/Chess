package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class LocalGameDao implements GameDao {
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int gameIdCounter = 1;

    @Override
    public int addGame(String gameName) throws DataAccessException {
        GameData gameData = new GameData(gameIdCounter, null, null,
                gameName, new ChessGame());

        games.put(gameIdCounter, gameData);

        gameIdCounter++;

        return gameData.gameID();
    }

    @Override
    public Collection<GameData> listCurrentGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public GameData getGameDataById(int gameId) throws DataAccessException {
        return games.get(gameId);
    }

    @Override
    public void replaceGame(int gameToReplaceId, GameData newData) throws DataAccessException {
        games.put(gameToReplaceId, newData);
    }

    @Override
    public void clear() throws DataAccessException {
        games = new HashMap<>();
    }
}
