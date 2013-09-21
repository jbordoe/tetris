package tetris;

/**
 *
 * @author Jesse-SouljaBoy-FanB
 */
public interface PlayerInterface {

    byte getNextMove();

    void clearPath();
    void setGame(Game game);
}
