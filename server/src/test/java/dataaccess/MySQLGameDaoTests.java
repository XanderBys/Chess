package dataaccess;

import dataaccess.mysql.MySQLGameDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
}
