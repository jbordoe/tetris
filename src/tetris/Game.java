package tetris;

import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Jesse Bordoe
 */
class Game implements Trainable {

    final static Random rand = new Random();
    Grid gridHandler;
    /** width of grid, in blocks */
    int gridWidth = 10;
    /** height of grid, in blocks */
    int gridHeight = 22;
    /** count of completed rows */
    int rows = 0;
    /** count of moves made by player. Each time a shape is set it
     * counts as one move
     */
    int turns = 0;
    /** score for player */
    // TODO: find out rules for tetris scoring and implement
    int score = 0;
    PlayerInterface player;
    BufferedImage highlight;
    /** time of the last downward movement (forced or manual) */
    long timeLastDropped;
    /** drop-rate of shapes, in milliseconds */
    int dropRate = 1200;

    /** the next shape */
    Shape nextShape;
    /** current descending shape */
    Shape currentShape;
    boolean shapeFalling;
    /** x position of current descending shape */
    private int shapeX;
    /** y position of current descending shape */
    private int shapeY;
    static boolean display;
    private BufferedImage gridImg;
    private static boolean running = true;

    /**
     * @return the shapeX
     */
    public /** x position of current descending shape */
    int getShapeX() {
        return shapeX;
    }

    /**
     * @param shapeX the shapeX to set
     */
    public void setShapeX(int shapeX) {
        this.shapeX = shapeX;
    }

    /**
     * @return the shapeY
     */
    public /** y position of current descending shape */
    int getShapeY() {
        return shapeY;
    }

    /**
     * @param shapeY the shapeY to set
     */
    public void setShapeY(int shapeY) {
        this.shapeY = shapeY;
    }

    /**
     * @return the playState
     */
    public PlayState getPlayState() {
        return playState;
    }

    /**
     * @param playState the playState to set
     */
    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }



    public static enum GameState {
        Play, New, GameOver, Quit, Evo, Paused
    };
    GameState state;

    public static enum PlayState {
        AI, FAST_AI, PLAYER
    }
    private PlayState playState;

    public static final byte MOVE_DOWN = 1;
    public static final byte MOVE_LEFT = 2;
    public static final byte MOVE_RIGHT = 3;
    public static final byte MOVE_ROTATE = 4;
    public static final byte MOVE_DROP = 5;
    public static final byte MOVE_NONE = 6;

    public Game(boolean evo) {
        display = false;
    }

    public Game() {
        this.playState = PlayState.AI;
    }

    public Game(int width, int height, PlayerInterface player, PlayState playState) {
        this.gridWidth = width;
        this.gridHeight = height;
        this.playState = playState;
        this.player = player;
        player.setGame(this);

        this.state = GameState.Paused;
    }

    public void getPlayerInput() {
        byte move = player.getNextMove();
        switch (move) {
            case MOVE_NONE:
                break;//do nothing
            case MOVE_LEFT:
                moveLeft();
                break;
            case MOVE_RIGHT:
                moveRight();
                break;
            case MOVE_DOWN:
                moveDown();
                break;
            case MOVE_ROTATE:
                rotate();
                break;
            case MOVE_DROP:
                drop();
                break;
        }
    }

    public void setState(GameState newState) {
        this.state = newState;
    }

    public void loop() {
        while(true) {
            nextFrame();
        }
    }

    public void nextFrame() {
        switch (state) {
            case Play: {
                getPlayerInput();
                if(playState != PlayState.AI
                        && System.currentTimeMillis() > timeLastDropped + dropRate) {
                    moveDown();
                    timeLastDropped = System.currentTimeMillis();
                }
                break;
            }
            case GameOver: {
                break;
            }
            case Paused: {
                break;
            }
            case Quit: {
                System.exit(0);
            }
        }
    }

    public static void setDisplay(boolean d) {
        display = d;
    }

    public void initGame() {
        rows = 0;
        turns = 0;
        gridHandler = new Grid(gridWidth, gridHeight);
        nextShape = new Shape();
        newShape();
    }

    public double runAI(int runs, double[] weights) {
        double meanScore = 0;
        for (int i = 0; i <
                runs; i++) {
            running = true;
            initGame();

            int thisScore = AI(weights);
            meanScore +=
                    thisScore;
        }

        return meanScore / runs;
    }

    private int AI(double[] weights) {
        while (running) {
            Pathfinder.setWeights(weights);
            //int[] state = Pathfinder.findNextMove(gridHandler, currentShape,getShapeX(),getShapeY());
            //if (state == null) {
            //    return rows;
            //}

            //setShapeX(state[0]);
            //setShapeY(state[1]);
            //currentShape.setRotationState(state[2]);
            //setGrid();

           // newShape();

        }


        return rows;
    }

    /**
     * create the next shape at the top of the grid
     */
    private void newShape() {
        timeLastDropped = System.currentTimeMillis();
        currentShape = nextShape;
        nextShape = new Shape();
        setShapeY(-1);
        setShapeX(3);
        if (lose()) {
            state = GameState.GameOver;
            running = false;
        }
    }

    /**
     * the game is lost when the next shape collides on arrival
     * @return <code>true</code> if the game has been lose <code>false</code> otherwise
     */
    private boolean lose() {
        return !gridHandler.legal(shapeX, shapeY, currentShape.getGrid());
    }

    private void moveDown() {
        if (canMoveDown()) {
            setShapeY(getShapeY() + 1);
            timeLastDropped = System.currentTimeMillis();
        } else {
            setGrid();
            player.clearPath();
        }
    }

    private void moveLeft() {
        if (canMoveLeft()) {
            setShapeX(getShapeX() - 1);
        }
    }

    private void moveRight() {
        if (canMoveRight()) {
            setShapeX(getShapeX() + 1);
        }
    }

    /** drop current shape instantly */
    private void drop() {
        gridHandler.removeLines();
        while (canMoveDown()) {
            setShapeY(getShapeY() + 1);
        }
        setGrid();
    }

    /** rotates current shape if legal, otherwise does nothing */
    private void rotate() {
        if (canRotate()) {
            currentShape.rotate();
        }
    }

    /**
     * @return <code>true</code> if the current shape can legally rotate in its current position (i.e no collisions)
     *  <code>false</code> otherwise
     */
    private boolean canRotate() {
        return gridHandler.legal(getShapeX(), getShapeY(),currentShape.getRotated());
    }

    /**
     * @return <code>true</code> if the current shape may move down from current position, <code>false</code> otherwise
     */
    private boolean canMoveDown() {
        return gridHandler.legal(getShapeX(), getShapeY() + 1, currentShape.getGrid());
    }

    /**
     * @return <code>true</code> if the current shape may move left from current position, <code>false</code> otherwise
     */
    private boolean canMoveLeft() {
        return gridHandler.legal(getShapeX() - 1, getShapeY(),currentShape.getGrid());
    }

    /**
     * @return <code>true</code> if the current shape may move right from current position, <code>false</code> otherwise
     */
    private boolean canMoveRight() {
        return gridHandler.legal(getShapeX() + 1, getShapeY(),currentShape.getGrid());
    }

    public Grid getGridHandler() {
        return gridHandler;
    }

    public byte[][] getCurrentGrid() {
        return gridHandler.updateGrid(getShapeX(), getShapeY(),currentShape.getGrid());
    }

    public Shape getCurrentShape() {
        return currentShape;
    }



    private void setGrid() {
        gridHandler.set(getShapeX(), getShapeY(),currentShape.getGrid());
        int lines = gridHandler.countLines(getShapeY());
        if (lines > 0) {
            rows += lines;
            if (display) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
            gridHandler.removeLines();
        }
        turns++;
        newShape();
    }

    //Trainable implementations
    public double rank(double[] vector) {
        return runAI(1, vector);
    }

    public double rank(int runs, double[] vector) {
        return runAI(runs, vector);
    }

    public double rank(int[] vector) {
        double[] dVector = new double[vector.length];
        for (int i = 0; i <
                dVector.length; i++) {
            dVector[i] = (double) vector[i];
        }

        return runAI(1, dVector);
    }

    public double rank(int runs, int[] vector) {
        double[] dVector = new double[vector.length];
        for (int i = 0; i <
                dVector.length; i++) {
            dVector[i] = (double) vector[i];
        }

        return runAI(runs, dVector);
    }

    public int getDimensions() {
        return 12;
    }

  
}
