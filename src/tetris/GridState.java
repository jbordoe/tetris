package tetris;

import java.util.Stack;

/**
 *
 * @author Jesse-SouljaBoy-FanB
 */
public class GridState {
    Stack<Byte> path;
    int x;
    int y;
    int rot;

    public GridState(int x, int y, int rot, Stack<Byte> path) {
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.path = path;
    }

}
