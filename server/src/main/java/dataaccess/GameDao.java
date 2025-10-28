package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;


public interface GameDao {
    /**
     * Adds an instance of GameData to the database
     *
     * @param gameName the name of the game to add
     * @return the ID of the added game
     * @throws DataAccessException for database errors
     */
    int addGame(String gameName) throws DataAccessException;

    /**
     * Gets a list of the current games in the database
     *
     * @return a Collection of GameData
     * @throws DataAccessException for database errors
     */
    Collection<GameData> listCurrentGames() throws DataAccessException;

    /**
     * Gets an instance of GameData corresponding to a specific game ID
     *
     * @param gameId the ID of the desired game
     * @return an instance of GameData corresponding to gameID, or null if there is no such instnace
     * @throws DataAccessException for database errors
     */
    GameData getGameDataById(int gameId) throws DataAccessException;

    /**
     * Updates the database with a new instance of GameData for changing game states
     *
     * @param gameToReplaceId the ID of the game to update
     * @param newData         the new GameData instance
     * @throws DataAccessException for database errors
     */
    void replaceGame(int gameToReplaceId, GameData newData) throws DataAccessException;

    /**
     * Deletes all games in the database
     *
     * @throws DataAccessException for database errors
     */
    void clear() throws DataAccessException, SQLException;
}
