package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDao;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDao implements GameDao {
    public static String gameTableName = "games";

    public MySQLGameDao() {
        DatabaseManager.createDatabase();
        DatabaseManager.createTable(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            gameID INT NOT NULL AUTO_INCREMENT,
                            whiteUsername VARCHAR(%d) DEFAULT NULL,
                            blackUsername VARCHAR(%d) DEFAULT NULL,
                            gameName VARCHAR(%d) NOT NULL,
                            game longtext NOT NULL,
                            PRIMARY KEY (gameID));
                        """, gameTableName, DatabaseManager.maxStringLength,
                DatabaseManager.maxStringLength, DatabaseManager.maxStringLength));
    }
    @Override
    public int addGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String addGameSQL = "INSERT INTO " + gameTableName + " (gameName, game) VALUES (?, ?);";
            ChessGame game = new ChessGame();
            String chessGameJSON = new Gson().toJson(game);

            try (var preparedStatement = conn.prepareStatement(addGameSQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, chessGameJSON);

                return executeStatementAndReturnKey(preparedStatement);
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to create new game", e);
        }
    }

    private int executeStatementAndReturnKey(PreparedStatement preparedStatement) throws SQLException {
        if (preparedStatement.executeUpdate() == 1) {
            try (ResultSet genereatedKeys = preparedStatement.getGeneratedKeys()) {
                genereatedKeys.next();
                return genereatedKeys.getInt(1);
            }
        } else {
            throw new SQLException("unable to insert new game");
        }
    }

    @Override
    public Collection<GameData> listCurrentGames() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            String getGamesSQL = "SELECT * FROM " + gameTableName;
            try (var preparedStatement = conn.prepareStatement(getGamesSQL)) {
                var rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    gameList.add(createGameFromResult(rs));
                }

                return gameList;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to access current game list", e);
        }
    }

    private GameData createGameFromResult(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }

        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public GameData getGameDataById(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String getGameSQL = "SELECT * FROM " + gameTableName + " WHERE gameID=?";
            try (var preparedStatement = conn.prepareStatement(getGameSQL)) {
                preparedStatement.setInt(1, gameId);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return createGameFromResult(rs);
                } else {
                    throw new DataAccessException("could not get game with ID " + gameId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("error accessing get game with ID " + gameId, e);
        }
    }

    @Override
    public void replaceGame(int gameToReplaceId, GameData newData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException, SQLException {
        DatabaseManager.deleteTable(gameTableName);
    }
}
