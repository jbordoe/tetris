package tetris;

/**
 *
 * @author Jesse Bordoe
 */
public class AIWeights {
    public static final short HEIGHT = 0;
    public static final short GAPS = 1;
    public static final short CONNECTED = 2;
    public static final short MAX_WELL = 3;
    public static final short WELL_SUM = 4;
    public static final short BLOCKS = 5;
    public static final short W_BLOCK = 6;
    public static final short ADJACENCY = 7;
    public static final short ALTERNATION = 8;
    public static final short LANDING_HEIGHT = 9;
    public static final short COL_TRANS = 10;
    public static final short ROW_TRANS = 11;
    public static final short LINE = 12;

    private int weightCount = 13;

    private double[] weights;

    public AIWeights() {
        weights = new double[weightCount];
        for (int i = 0; i < weightCount; i++) {
            weights[i] = 0.0;
        }
    }

    public AIWeights(double weights[]) {
        this.weights = new double[weightCount];
        for (int i = 0; i < weightCount; i++) {
            this.weights[i] = 0.0;
        }
    }

    public void setWeight(short index, double value) {
        weights[index] = value;
    }

    public double[] getWeights() {
        return weights;
    }

}
