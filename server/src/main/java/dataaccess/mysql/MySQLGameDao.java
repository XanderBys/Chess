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
import java.util.Collection;
import java.util.List;

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
    public void clear() throws DataAccessException, SQLException {
        DatabaseManager.deleteTable(gameTableName);
    }
}
