package tetris;

/**
 *
 * @author Jesse Bordoe
 */
public interface Trainable {
    
    public double rank(double[] vector);

    public double rank(int runs, double[] vector);

    public double rank(int[] vector);

    public double rank(int runs, int[] vector);

    public int getDimensions();

}
