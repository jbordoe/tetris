package tetris;

/**
 *
 * @author Jesse Bordoe
 */
public class Grid {
    byte[][] grid;
    /** width of grid, in blocks */
    int width;
    /** height of grid, in blocks */
    int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new byte[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = 0;
            }
        }
    }

    public byte[][] copyGrid() {
        byte[][] newGrid = new byte[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newGrid[i][j] = grid[i][j];
            }
        }
        return newGrid;
    }

    /**
     * place a shape in the grid permanently
     * @param x
     * @param y
     * @param shapeGrid
     */
    public void set(int x, int y, byte[][] shapeGrid) {
        grid = updateGrid(x, y, shapeGrid);
    }

    /**
     * identify and flag any completed horizonatal lines in the grid
     */
    public boolean checkForLines(int shapeY) {
        boolean linesFound = false;
        for (int y = shapeY; y < shapeY + 4; y++) {
            boolean full = true;
            if (y >= height || y < 0) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                if (grid[x][y] < 1) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesFound = true;
                for (int x = 0; x < width; x++) {
                    grid[x][y] = -1;
                }
            }
        }
        return linesFound;
    }


   public int countLines(int shapeY) {
        int linesFound = 0;
        for (int y = shapeY; y < shapeY + 4; y++) {
            boolean full = true;
            if (y >= height || y < 0) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                if (grid[x][y] < 1) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesFound ++;                
                for (int x = 0; x < width; x++) {
                    grid[x][y] = -1;
                }

            }
        }
        return linesFound;
    }

    public byte[][] getGrid(){
        return grid;
    }

    /**
     * remove completed horizontal lines from the grid, move everything above them downwards
     * @return <code>true if one or more complete lines were
     */
    public void removeLines(){
        int newY = height-1;
        byte[][] newGrid = new byte[width][height];
        //create a new empty grid
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                newGrid[i][j] = 0;
            }
        }
        for(int y = height-1; y >= 0;y--){
            if(grid[0][y]>=0){
                for(int i = 0; i < width; i++){
                    newGrid[i][newY] = grid[i][y];
                }
                newY--;
            }
        }
        grid = newGrid;
    }

    /**
     * update the position of the shape in the grid
     */
    public byte[][] updateGrid(int x, int y, byte[][] shapeGrid) {
        byte[][] tempGrid = copyGrid();
        for (int i = 0; i < shapeGrid.length; i++) {
            for (int j = 0; j < shapeGrid[0].length; j++) {
                if (shapeGrid[i][j] > 0 && i + x >= 0 && j + y >= 0) {
                    tempGrid[i + x][j + y] = shapeGrid[i][j];
                }
            }
        }
        return tempGrid;
    }
    
    /**
     *
     * @param x horizontal position of shape
     * @param y vertical position of shape
     * @param shapeGrid
     * @return <code>true</code> if the shape can exist in the given position on the grid <code>false</code> otherwise
     */
    public boolean legal(int x,int y,byte[][]shapeGrid){
        for (int i = 0; i < shapeGrid.length; i++) {
            for (int j = 0; j < shapeGrid[0].length; j++) {
                if (shapeGrid[i][j] > 0) {
                    //see if shape will escape boundaries
                    if (i + x < 0 || i + x >= width || j + y >= height) {
                         return false;
                    }
                    //see if collisions occur
                    if (j + y >= 0 && grid[i + x][j + y] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }


}


