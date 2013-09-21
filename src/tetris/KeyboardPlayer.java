package tetris;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Jesse-SouljaBoy-FanB
 */
public class KeyboardPlayer implements PlayerInterface{

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean downPressed = false;
    private boolean upPressed = false;
    private boolean dropPressed = false;

    Game game;

    public KeyboardPlayer() {}

    public void clearPath() {}

    public void setGame(Game game) {
        this.game = game;
    }

    //TODO: should probably return an array of moves for multiple key presses
    public byte getNextMove() {
        if (leftPressed) { return Game.MOVE_LEFT; }
        if (rightPressed) { return Game.MOVE_RIGHT; }
        if (downPressed) { return Game.MOVE_DOWN; }
        if (upPressed) { return Game.MOVE_ROTATE; }
        if (dropPressed) { return Game.MOVE_DROP; }

        return Game.MOVE_NONE;
    }

    public KeyAdapter getKeyAdapter() {
        return new KeyInputHandler();
    }

    public class KeyInputHandler extends KeyAdapter {

        long leftCounter = 0;
        long rightCounter = 0;
        long downCounter = 0;
        long upCounter = 0;

        public void keyPressed(KeyEvent e) {
            long time = System.currentTimeMillis();
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (leftCounter == 0 || time - leftCounter > 50) {
                    //leftPressed = true;
                    leftCounter = time;
                } else {
                    leftPressed = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (rightCounter == 0 || time - rightCounter > 50) {
                    //rightPressed = true;
                    rightCounter = time;
                } else {
                    rightPressed = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                dropPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (upCounter == 0 || time - upCounter > 50) {
                    //upPressed = true;
                    upCounter = time;
                } else {
                    upPressed = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = true;
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
                leftCounter = 0;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
                rightCounter = 0;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                dropPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = false;
                upCounter = 0;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = false;
                upCounter = 0;
            }
        }
    }

}
