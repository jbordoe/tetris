package tetris;

import java.awt.image.BufferedImage;

/**
 *
 * @author Jesse Bordoe
 */
public interface GridRenderer {

    BufferedImage renderGrid(Game game);
    void setBlockWidth(int width);
    void setBlockHeight(int height);
}
