package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDao implements GameDao {
    @Override
    public int addGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public Collection<GameData> listCurrentGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData getGameDataById(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public void replaceGame(int gameToReplaceId, GameData newData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
