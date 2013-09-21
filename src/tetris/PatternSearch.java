package tetris;

import java.util.Random;
/**
 *
 * @author Jesse Bordoe
 */
public class PatternSearch {
    private static final Trainable problem = new Game(false);
    private static final int runs = 10;
    private static final int testRuns = 2;

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args){
        PatternSearch search = new PatternSearch();
        search.run();
    }

    public static void run(){
        /* initial step size */
        double stepSize = 1;
        double bestScore;

        //double[] vector = new double[problem.getDimensions()];
        //for(int i = 0; i < vector.length; i ++) {
        //    vector[i] = 0;
        //}
        //zeros = vector.clone();
        double[] vector = new double[]{ 0.0, -1.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, -1.0, -1.0, -1.0 };
        System.out.println("getting initial position score...");
        bestScore = problem.rank(testRuns,vector);
        System.out.println((int)bestScore);
        for(int run = 0; run < runs; run ++){
            System.out.print("run-"+(run+1)+"...");
            long stamp = System.currentTimeMillis();
            double[] newVector = new double[problem.getDimensions()];//zeros.clone();
            boolean better = false;
            for(int i = 0; i < vector.length; i++){
                double[] stepUp = vector.clone();
                stepUp[i] += stepSize;
                double newScore = problem.rank(testRuns,stepUp);
                if(newScore > bestScore){
                    bestScore = newScore;
                    newVector = stepUp;
                    better = true;
                }
                double[] stepDown = vector.clone();
                stepDown[i] -= stepSize;
                newScore = problem.rank(testRuns,stepDown);
                if(newScore > bestScore){
                    bestScore = newScore;
                    newVector = stepDown;
                    better = true;
                }
            }
            if(better){
                vector = newVector;
                System.out.println("took "+((System.currentTimeMillis()-stamp)/1000)+"s\n"
                        +"step size: "+stepSize+", best score: "+(int)bestScore);
                System.out.println("pos: "+toString(vector));
            } else {
                System.out.println("reducing step size...");
                stepSize = stepSize/2;
            }
        }
        String output = "";
        for(int i = 0; i < vector.length; i ++){
            output = "\t"+vector[i]+"\n";
        }
        System.out.println("solution: \n"+output);
    }

    public static String toString(double[] v){
        String o = "{ "+v[0];
        for(int i = 1; i < v.length; i++ ){
            o+=", "+v[i];
        }

        return o+" }";
    }
}
