package dataaccess;

import dataaccess.mysql.MySQLGameDao;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

public class MySQLGameDaoTests extends MySQLDaoTests {
    @BeforeEach
    public void setUp() throws SQLException {
        gameDao = new MySQLGameDao();
        gameDao.clear();
    }

    @AfterEach
    public void takeDown() throws SQLException {
        gameDao.clear();
    }

    @Test
    public void clearTest() throws SQLException {
        gameDao.addGame(gameName);
        gameDao.addGame("1");
        gameDao.addGame("abowjwg");

        gameDao.clear();

        assertTableEmpty(MySQLGameDao.gameTableName);
    }

    @Test
    public void addGameNormal() {
        int id1 = gameDao.addGame(gameName);
        int id2 = gameDao.addGame("game 2");

        Assertions.assertEquals(1, id1);
        Assertions.assertEquals(2, id2);

        assertTableCount(MySQLGameDao.gameTableName, 2);
    }

    @Test
    public void addGameLongName() {
        gameDao.addGame(gameName);

        Assertions.assertThrows(DataAccessException.class, () -> gameDao.addGame("a".repeat(256)));
    }

    @Test
    public void getGameListNormal() {
        String[] names = {"game1", "game2", "game3"};
        gameDao.addGame(names[0]);
        gameDao.addGame(names[1]);
        gameDao.addGame(names[2]);

        Collection<GameData> games = gameDao.listCurrentGames();

        for (GameData game : games) {
            Assertions.assertNotNull(game.gameName());
            Assertions.assertNotNull(game.gameID());
            Assertions.assertNotNull(game.game());
        }
    }

    @Test
    public void getGameListEmpty() {
        Collection<GameData> games = gameDao.listCurrentGames();
        Assertions.assertEquals(0, games.size());
    }
}
