package tetris;

import java.util.Stack;

/**
 *
 * @author Jesse Bordoe
 */
public class AIPlayer implements PlayerInterface{

    private double[] weights = new double[]{
        -0.20315298219627706,
        -0.19269713510407616,
        -0.9945498213990135,
        -0.07923737184451984,
        -0.1627925353995784,
        0.018432736372521897,
        0.12123347586320943,
        -0.1980587628350046,
        0.10383619130407325,
        -0.12341878537621037,
        0.0,
        -0.3677699284288867
    };

    Game game;
    
    Stack<Byte> currentPath = new Stack<Byte>();


    public AIPlayer(double[] weights) {
        this.weights = weights;
        Pathfinder.setWeights(this.weights);       
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void clearPath() {
        currentPath.clear();
    }

    public byte getNextMove() {
        if (currentPath.empty() ) {
            currentPath = Pathfinder.findMoves(game);
        }
        return currentPath.pop();
    }



}
