package tetris;

import java.util.HashMap;

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

    public static final HashMap<Short, Weight> weightData = new HashMap<Short, Weight>();
    {
        // TODO: Look up, revise and document each of these weights
        weightData.put( HEIGHT, new Weight("Max Height", "Maximum height of blocks") );
        weightData.put( GAPS, new Weight("Gap Count", "No. of gaps on the grid (in blocks)") );
        weightData.put( CONNECTED, new Weight("Connected", "???") );
        weightData.put( MAX_WELL, new Weight("Max Well Depth", "Maximum well depth") );
        weightData.put( WELL_SUM, new Weight("Wells", "Number of wells") );
        weightData.put( BLOCKS, new Weight("Blocks", "Count of blocks?") );
        weightData.put( W_BLOCK, new Weight("W Block", "???") );
        weightData.put( ADJACENCY, new Weight("Adjacency", "Count of blocks adjacent to shape in landing position") );
        weightData.put( ALTERNATION, new Weight("Alternation", "???") );
        weightData.put( LANDING_HEIGHT, new Weight("Landing Height", "Height at which shape lands") );
        weightData.put( COL_TRANS, new Weight("Col. Transitions", "Vertical transitions between filled and empty spaces") );
        weightData.put( ROW_TRANS, new Weight("Row Transitions", "Horizontal transitions between filled and empty spaces") );
        weightData.put( LINE, new Weight("Lines", "Count of filled rows") );
    }
    
    private static final int weightCount = weightData.size();

    private double[] weights;

    public AIWeights() {
        weights = new double[weightCount];
        for (int i = 0; i < weightCount; i++) {
            weights[i] = 0.0;
        }
    }

    /**
     * S
     * @param index weight to set. Use this class public constants to refer to a given index
     * @param value value to set weight to
     */
    public void setWeight(short index, double value) {
        weights[index] = value;
    }

    public double[] getWeights() {
        return weights;
    }
    
    /**
     * 
     * @param weights 
     */
    public void setWeights(double[] weights) {
        // TODO: ensure arg array is same size as internal one
        this.weights = weights;
    }

    /**
     * 
     * @param weightKey
     * @return 
     */
    public Weight getWeightData(short weightKey) {
        return weightData.get(weightKey);
    }

    public class Weight {
        private String description;
        private String label;

        public Weight(String label, String description){
            this.description = description;
            this.label = label;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }

        /**
         * @param label the label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }
    }

}
