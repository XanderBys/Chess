package dataaccess;

import model.GameData;

import java.util.Collection;


public interface GameDao {
    int addGame(String gameName) throws DataAccessException;

    Collection<GameData> listCurrentGames() throws DataAccessException;

    GameData getGameDataById(int gameId) throws DataAccessException;

    void replaceGame(int gameToReplaceId, GameData newData) throws DataAccessException;

    void clear() throws DataAccessException;
}
