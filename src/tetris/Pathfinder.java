package tetris;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Using A* search, determines the route (if any) a tetris shape may take from it's current position to a desired
 * location
 * @author Jesse Bordoe
 */
public class Pathfinder {

    private static int goalX;
    private static int goalY;
    private static int goalRot;
    private static int rotationStates;
    private static double[] weights;

    /**
     * find legal end-rotationStates for the current shape
     * @param grid
     * @param shape
     * @param startX
     * @param startY
     * @return a list of legal moves
     */
    public static Stack<Byte> findMoves(Game game) {
        Grid grid = game.getGridHandler();
        Shape shape = game.getCurrentShape();

        int startX = game.getShapeX();
        int startY = game.getShapeY();

        ArrayList<State> moves = new ArrayList<State>();
        rotationStates = shape.getStates();
        //try all combinations of postion and rotation
        for (int rotation = 0; rotation < rotationStates; rotation++) {
            for (int x = -4; x < grid.getWidth() + 4; x++) {
                for (int y = -1; y < grid.getHeight() + 4; y++) {
                    if (grid.legal(x, y, shape.getState(rotation)) && !grid.legal(x, y + 1, shape.getState(rotation))) {
                        moves.add(new State(x, y, rotation));
                    }
                }
            }
        }
        Stack<Byte> path = new Stack<Byte>();
        for (State s : moves) {
            rank(s, grid, shape);
        }

        while (true) {
            State selected = getBestState(moves);
            path = findPathToState(grid, shape, startX, startY, selected.getX(), selected.getY(), selected.getRot());
            if (path != null && !path.empty()) {
                break;
            } else {
                moves.remove(selected);
            }
        }
        return path;
    }

    public static void setWeights(double[] weights) {
        Pathfinder.weights = weights;
    }

    /**
     * given a list of legal game states, rank them all and return the best one
     * @param rotationStates
     * @return
     */
    private static State getBestState(ArrayList<State> states) {
        double best = Double.NEGATIVE_INFINITY;
        State bestState = null;
        for (State s : states) {
            if (s.getRank() > best) {
                bestState = s;
                best = s.getRank();
            } else if (s.getRank() == best) {
                if (bestState != null) {
                    //in the event of a tie, select leftmost position
                    bestState = s.getX() < bestState.getX() ? s : bestState;
                } else {
                    bestState = s;
                }
            }
        }
        return bestState;
    }

    /**
     * using A* search, find a path (if it exists) to the given goal state
     * @param grid
     * @param shape
     * @param startX
     * @param startY
     * @param destX
     * @param destY
     * @param destRot
     * @return array representation of path to follow, or null if no such path exists
     */
    private static Stack<Byte> findPathToState(Grid grid, Shape shape, int startX, int startY, int destX, int destY, int destRot) {
        rotationStates = shape.getStates();
        goalX = destX;
        goalY = destY;
        goalRot = destRot;

        Stack<Byte> path = new Stack<Byte>();
        /** our desired state */
        State goal = new State(destX, destY, destRot);
        /** out starting state */
        State start = new State(startX, startY, shape.getRotationState());

        //ensure goal position is legal
        if (!grid.legal(destX, destY, shape.getState(destRot))) {
            return null;
        }
        //initialise closed 
        boolean[][][] closed = new boolean[grid.getWidth() + 8][grid.getHeight() + 8][rotationStates];
        for (int i = 0; i < closed.length; i++) {
            for (int j = 0; j < closed[0].length; j++) {
                for (int k = 0; k < rotationStates; k++) {
                    closed[i][j][k] = false;
                }
            }
        }
        /** the 'openSet set' - rotationStates we may potentially explore next */
        ArrayList<State> openSet = new ArrayList<State>();
        openSet.add(start); //initialize with current state

        while (!openSet.isEmpty()) {
            State currentBestState = getBestNode(openSet); //get the node with the lowest f-score

            if (currentBestState.equals(goal)) {
                path = makePath(currentBestState);
                break;
            }
            openSet.remove(currentBestState);
            // add current state to closed set
            closed[currentBestState.getX() + 4][currentBestState.getY() + 4][currentBestState.getRot()] = true;

            for (int[] neighbour : currentBestState.getNeighbours()) {
                boolean tentativeIsBetter;
                int neighbourX = neighbour[0];
                int neighbourY = neighbour[1];
                int neighbourRotation = neighbour[2];
                
                if (closed[neighbourX + 4][neighbourY + 4][neighbourRotation]) {
                    continue;
                }
                //make sure this is a legal state
                if (grid.legal(neighbourX, neighbourY, shape.getState(neighbourRotation))) {
                    int neighbourCost = currentBestState.getCost() + 1;
                    byte transitionToNeighbour = (byte)neighbour[3];
                    
                    State validNeigbourState = new State(
                        neighbourX,
                        neighbourY,
                        neighbourRotation,
                        neighbourCost,
                        transitionToNeighbour,
                        currentBestState
                    );
                    openSet.add(validNeigbourState);
                    closed[neighbour[0] + 4][neighbour[1] + 4][neighbour[2]] = true;
                    tentativeIsBetter = true;
                }
            }
        }
        return path;
    }

    /**
     * Traverses hierarchy of state to extract stack of moves to perform in order
     * to move from beginning state to goal state
     * @param s
     * @return
     */
    private static Stack<Byte> makePath(State goalState) {
        Stack<Byte> path = new Stack<Byte>();
        //final move should 'lock' piece in place;
        path.push(Game.MOVE_DOWN);
        State currentState = goalState;
        while (currentState.getParent() != null) {
            path.push(currentState.getTransition());
            currentState = currentState.getParent();
        }
        return path;
    }

    /**
     * give a score to a game state
     * @param state
     */
    private static void rank(State state, Grid grid, Shape shape) {
        
        double heightWeight = weights[0];
        double gapWeight = weights[1];
        double connectedWeight = weights[2];
        double maxWellWeight = weights[3];
        double wellSumWeight = weights[4];
        double blockWeight = weights[5];
        double wBlockWeight = weights[6];
        double adjacencyWeight = weights[7];
        double altWeight = weights[8];
        double landingWeight = weights[9];
        double colTransWeight = weights[10];
        double rowTransWeight = weights[11];
        double lineWeight = weights[12];

        byte[][] updatedGrid = grid.updateGrid(state.getX(), state.getY(), shape.getState(state.getRot()));
        int[] gapData = countGaps(updatedGrid);
        int[] wellData = getWellData(updatedGrid);
        int[] blockData = getBlockData(updatedGrid);


        int lines = countLines(updatedGrid, state.getY());
        //int blockades = countBlockades(updatedGrid);
        int height = getHeight(updatedGrid) - lines;
        int gaps = gapData[0];
        int connectedGaps = gapData[1];
        int maxWell = wellData[0];
        int wellSum = wellData[1];
        int blocks = blockData[0];
        int weightedBlocks = wellData[1];
        int adjacent = countAdjacent(grid.getGrid(), shape.getState(state.getRot()), state.getX(), state.getY());
        int alt = getAltDiff(updatedGrid);
        int landingHeight = grid.height - state.getY();
        int colTransitions = getColumnTransitions(updatedGrid);
        int rowTransitions = getRowTransitions(updatedGrid);

        double rank = 0;
        rank += height * heightWeight;
        rank += lines * lineWeight;
        rank += gaps * gapWeight;
        rank += connectedGaps * connectedWeight;
        rank += maxWell * maxWellWeight;
        rank += wellSum * wellSumWeight;
        rank += blocks * blockWeight;
        rank += weightedBlocks * blockWeight;
        rank += adjacent * adjacencyWeight;
        rank += alt * altWeight;
        rank += landingHeight * landingWeight;
        rank += colTransitions * colTransWeight;
        rank += rowTransitions * rowTransWeight;

        state.setRank(rank);
    }

    private static int countLines(byte[][] gameGrid, int shapeY) {
        int linesFound = 0;
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        for (int y = shapeY; y < shapeY + 4; y++) {
            boolean full = true;
            if (y >= height || y < 0) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                if (gameGrid[x][y] < 1) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesFound++;
            }
        }
        return linesFound;
    }

    private static int[] getWellData(byte[][] gameGrid) {
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        int max = 0;
        int sum = 0;
        for (int x = 0; x < width; x++) {
            int wellDepth = 0;
            for (int y = 0; y < height; y++) {
                boolean left, right;
                left = x == 0 ? true : gameGrid[x - 1][y] > 0;
                right = x == width - 1 ? true : gameGrid[x + 1][y] > 0;
                if (left && right) {
                    wellDepth++;
                }
            }
            sum += wellDepth;
            if (wellDepth > max) {
                max = wellDepth;
            }
        }
        return new int[]{max, sum};
    }

    private static int[] getBlockData(byte[][] gameGrid) {
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        int blocks = 0;
        int weightedBlocks = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 1; y <= height; y++) {
                if (gameGrid[x][height - y] > 0) {
                    blocks++;
                    weightedBlocks += y;
                }
            }
        }
        return new int[]{blocks, weightedBlocks};
    }

    private static int getAltDiff(byte[][] gameGrid) {
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        int max = 0;
        int min = height;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (gameGrid[x][y] > 0) {
                    if (y < min) {
                        min = y;
                    } else if (y > max) {
                        max = y;
                    }
                }
            }
        }
        return max - min;
    }

    private static int getRowTransitions(byte[][] gameGrid) {
        int rowTransitions = 0;
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (gameGrid[x][y] > 0 && gameGrid[x + 1][y] == 0) {
                    rowTransitions++;
                } else if (gameGrid[x][y] == 0 && gameGrid[x + 1][y] > 0) {
                    rowTransitions++;
                }
            }
        }
        return rowTransitions;
    }

    private static int getColumnTransitions(byte[][] gameGrid) {
        int colTransitions = 0;
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height - 1; y++) {
                if (gameGrid[x][y] > 0 && gameGrid[x][y + 1] == 0) {
                    colTransitions++;
                } else if (gameGrid[x][y] == 0 && gameGrid[x][y + 1] > 0) {
                    colTransitions++;
                }
            }
        }
        return colTransitions;
    }

    private static int[] countGaps(byte[][] gameGrid) {
        int gaps = 0;
        int connectedGaps = 0;
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        for (int x = 0; x < width; x++) {
            boolean connected = false;
            boolean gap = false;
            for (int y = 0; y < height; y++) {
                if (gameGrid[x][y] > 0) {
                    gap = true;
                    connected = true;
                }
                if (gap && gameGrid[x][y] == 0) {
                    gaps++;
                }
                if (connected && gameGrid[x][y] == 0) {
                    connectedGaps++;
                    connected = false;
                }
            }
        }
        return new int[]{gaps, connectedGaps};
    }

    private static int countBlockades(byte[][] gameGrid) {
        int blockades = 0;
        int height = gameGrid[0].length;
        int width = gameGrid.length;
        for (int x = 0; x < width; x++) {
            boolean blockade = false;
            for (int y = height - 1; y >= 0; y--) {
                if (gameGrid[x][y] == 0) {
                    blockade = true;
                }
                if (blockade && gameGrid[x][y] > 0) {
                    blockades++;
                }
            }
        }
        return blockades;
    }

    private static int countAdjacent(byte[][] gameGrid, byte[][] shapeGrid, int x, int y) {
        int adjacent = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shapeGrid[i][j] > 0 && (y + j) >= 0) {
                    //to the left
                    if ((x + i) - 1 >= 0 && gameGrid[(x + i) - 1][y + j] > 0) {
                        adjacent++;
                    }
                    //to the right
                    if ((x + i) + 1 < gameGrid.length && gameGrid[x + i + 1][y + j] > 0) {
                        adjacent++;
                    }
                    //above
                    if ((j + y) - 1 >= 0 && gameGrid[x + i][(y + j) - 1] > 0) {
                        adjacent++;
                    }
                    //below
                    if ((j + y) + 1 < gameGrid[0].length && gameGrid[x + i][y + j + 1] > 0) {
                        adjacent++;
                    }
                    if ((j + y) + 1 < gameGrid[0].length && gameGrid[x + i][y + j + 1] == gameGrid.length) {
                        adjacent++;
                    }

                }
            }
        }
        return adjacent;
    }

    private static int getHeight(byte[][] gameGrid) {
        for (int y = 0; y < gameGrid[0].length; y++) {
            for (int x = 0; x < gameGrid.length; x++) {
                if (gameGrid[x][y] > 0) {
                    return gameGrid[0].length - y;
                }
            }
        }
        return 0;
    }

    /**
     * heuristic estimate
     * @param currentX current bestCurrentState position of shape
     * @param currentY current y position of shape
     * @param destX bestCurrentState value of destination
     * @param destY y value of destination
     * @return euclidian distance between current position and destination, plus mimimum rotations required
     */
    private static double h(int currentX, int currentY, int currentRot, int destX, int destY, int destRot) {
        return Math.sqrt(Math.pow(destX - currentX, 2) + Math.pow(destY - currentY, 2)) +
                (((rotationStates - currentRot) + destRot) % rotationStates); //# of rotations required
    }

    /**
     * @param openSet the set of nodes to traverse
     * @return the node in the set with the lowest f-score
     */
    private static State getBestNode(ArrayList<State> openSet) {
        double bestScore = Double.MAX_VALUE;
        State bestNode = null;
        for (State n : openSet) {
            if (n.getEstimatedTotalCost() < bestScore) {
                bestScore = n.getEstimatedTotalCost();
                bestNode = n;
            }
        }
        return bestNode;
    }

    /**
     * represents a state comprised of bestCurrentState and y coordinates, and rotation
     */
    private static class State {
        /** estimated cost from this state to goal state */
        private double estimate;
        /** cost to reach this state */
        private int cost;
        private State parent;
        private int x;
        private int y;
        private int rot;
        /** the move that brought us to this state */
        private byte transition;
        private double rank;

        public State(int x, int y, int rot, int cost, byte transition, State parent) {
            this.x = x;
            this.y = y;
            this.rot = rot;
            this.estimate = h(x, y, rot, goalX, goalY, goalRot);
            this.cost = cost;
            this.parent = parent;
            this.transition = transition;
        }

        public State(int x, int y, int rot) {
            this.x = x;
            this.y = y;
            this.rot = rot;
            this.cost = 0;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getRot() {
            return rot;
        }

        public byte getTransition() {
            return transition;
        }

        public State getParent() {
            return parent;
        }

        /**
         *
         * @return
         */
        public double getEstimatedTotalCost() {
            return cost + estimate;
        }

        public int getCost() {
            return cost;
        }

        public boolean equals(State n) {
            boolean equal = false;
            if (n.getX() == this.x && n.getY() == this.y && n.getRot() == this.rot) {
                equal = true;
            }
            return equal;
        }

        public int[][] getNeighbours() {
            return new int[][]{
                        {x - 1, y, rot, Game.MOVE_LEFT}, //represents movement to left
                        {x, y + 1, rot, Game.MOVE_DOWN}, //represents movement down
                        {x + 1, y, rot, Game.MOVE_RIGHT}, //represents movement to right
                        {x, y, (rot + 1) % rotationStates, Game.MOVE_ROTATE} //represents a rotation
                    };
        }

        @Override
        public String toString() {
            return "X: " + x + ",Y: " + y + ",Rot: " + rot + ",H: " + estimate + ",F: " + getEstimatedTotalCost();
        }

        /**
         * @return the rank
         */
        public double getRank() {
            return rank;
        }

        /**
         * @param rank the rank to set
         */
        public void setRank(double rank) {
            this.rank = rank;
        }
    }
}
