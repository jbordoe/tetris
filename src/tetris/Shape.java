package tetris;

import java.util.Random;

/**
 *
 * @author Jesse Bordoe
 */
public class Shape {
    final static Random rand = new Random();
    /** array of possible block layouts */
    final static byte[][][][] shapes = {
        //I
        {{{0,1},{1,1},{2,1},{3,1}},{{1,0},{1,1},{1,2},{1,3}}},
        //J
        {{{0,1},{1,1},{2,1},{2,2}},{{1,0},{1,1},{1,2},{0,2}},{{0,0},{0,1},{1,1},{2,1}},{{1,2},{1,1},{1,0},{2,0}}},
        //L
        {{{0,1},{0,2},{1,1},{2,1}},{{0,0},{1,0},{1,1},{1,2}},{{0,1},{1,1},{2,1},{2,0}},{{1,0},{1,1},{1,2},{2,2}}},
        //O
        {{{1,1},{2,1},{1,2},{2,2}}},
        //S
        {{{0,2},{1,2},{1,1},{2,1}},{{2,2},{2,1},{1,1},{1,0}}},
        //T
        {{{2,2},{2,1},{3,2},{1,2}},{{2,2},{2,1},{3,2},{2,3}},{{2,2},{2,3},{3,2},{1,2}},{{2,2},{2,3},{2,1},{1,2}}},
        //Z
        {{{2,2},{1,2},{1,1},{0,1}},{{2,0},{2,1},{1,1},{1,2}}}
    };
    int rotation = 0;
    /** indicator of block colour */
    byte col;
    int size;
    byte[][] grid;
    /** indicator of shape */
    int selection;
    public Shape(){
        //TODO: tweak selection of blocks
        selection = rand.nextInt(shapes.length); //initialize block at random
        //selection = 0; //force 'I' shape
        col = (byte)(rand.nextInt(7)+1); //select a colour at random
        size = shapes[selection][0].length;
        grid = fillGrid(0); //create grid for this shape
    }

    public byte[][] getGrid(){
        return grid;
    }

    public int getStates() {
        return shapes[selection].length;
    }

    public int getRotationState() {
        return rotation;
    }

    public void setRotationState(int r) {
        this.rotation = r;
        grid = fillGrid(rotation);
    }

    public byte[][] getState(int r){
        return fillGrid(r);
    }
    /**
     * return the grid we'd have after one rotation, without actually rotating this shape
     * @return
     */
    public byte[][] getRotated(){
        return fillGrid((rotation+1) % shapes[selection].length);
    }

    private byte[][] fillGrid(int r){
        byte[][] tempGrid = new byte[size][size];
        for(int i = 0; i < size; i++){
            for( int j = 0; j < size; j++){
                tempGrid[i][j] = 0;
            }
        }
        for(int i = 0; i < 4; i++){
            tempGrid[shapes[selection][r][i][0]][shapes[selection][r][i][1]] = col;
        }
        return tempGrid;
    }
    
    /**
     * rotates this shape
     */
    public void rotate(){
        rotation = (rotation+1) % shapes[selection].length;
        grid = fillGrid(rotation);
    }



}
