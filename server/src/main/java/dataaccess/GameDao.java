package dataaccess;

import model.GameData;

import java.util.Collection;


public interface GameDao {
    int addGame(String gameName);

    Collection<GameData> listCurrentGames();

    GameData getGameDataById(int gameId);

    void clear();
}
