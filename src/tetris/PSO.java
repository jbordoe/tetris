package tetris;

import java.util.Random;

/**
 *
 * @author Jesse Bordoe
 */
public class PSO {

    private static final Random rand = new Random();
    private static final Trainable problem = new Game(false);
    private static final int swarmSize = 63;
    private static final int runs = 100;
    private static Particle bestParticle;
    private static double[] bestVector = null;
    private static double bestScore = -1;
    private static final double φp = 1.6319; //phi parameter
    private static final double φg = 0.6239; //phi parameter
    private static final double ω = 0.6571; //omega
    private static final int testRuns = 1;


    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args){
        PSO pso = new PSO();
        pso.run();
    }

    public static void run(){
        //initialize population
        System.out.println("Initializing population...");
        Particle[] swarm = new Particle[swarmSize];
        for(int i = 0; i < swarmSize; i ++){
            double[] vector = new double[12];
            for(int j = 0; j > 12; j ++) {
                vector[j] = 1-(rand.nextDouble()*2);
            }
            swarm[i] = new Particle(vector);
            double score = problem.rank(testRuns, vector);
            //System.out.println(score);
            if(score>bestScore){
                bestScore = score;
                bestVector = vector;
                bestParticle = swarm[i];
            }
            swarm[i].setScore(score);
            
        }
        bestParticle = null;
        for (int run = 0; run < runs; run++) {
            long stamp = System.currentTimeMillis();
            double mean = 0;
            System.out.println("run-" + (run + 1));
            for (int i = 0; i < swarmSize; i++) {
                double rp = rand.nextInt();
                double rg = rand.nextInt();
                swarm[i].update(rp, rg);
                double score = problem.rank(testRuns, swarm[i].getVector());
                mean += score;
                swarm[i].setScore(score);
                if (score > bestScore) {
                    bestScore = score;
                    bestVector = swarm[i].getVector();
                    bestParticle = swarm[i];
                }                
            }
            System.out.println(bestScore);
            System.out.println(//"Best: "+bestParticle.getScore()+
                 "Mean: "+mean/swarmSize+
                    ", time: "+(System.currentTimeMillis()-stamp)/1000+"s");
        }
        System.out.println("Done!\nBest Result\n"+bestParticle.toString());
    }

    private static class Particle {
        private double[] vector;
        private double score;
        private double[] velocity;
        private double[] best;
        private double myBestScore = 0;

        public Particle(double[] weights) {
            this.vector = weights;
            this.best = weights.clone();
            this.velocity = new double[weights.length];
            for(int i =0; i < velocity.length; i++){
                velocity[i] = (rand.nextDouble()*2);
            }
        }

        public void update(double rp, double rg){
            velocity = add(mul(φg*rg, subtract(bestVector,vector)),
                    add(mul(ω,velocity) , mul(φp*rp, subtract(best,vector))));
            vector = add(vector,velocity);
        }

        public double[] subtract(double[] v1, double[] v2){
            double[] res = new double[v1.length];
            for(int i = 0; i < v1.length; i ++){
                res[i] = v1[i] - v2[i];
            }
            return res;
        }

        public double[] add(double[] v1, double[] v2){
            double[] res = new double[v1.length];
            for(int i = 0; i < v1.length; i ++){
                res[i] = v1[i] + v2[i];
            }
            return res;
        }

        public double[] mul (double scalar, double[] vector){
            double[] res = new double[vector.length];
            for(int i = 0; i < vector.length; i ++){
                res[i] = vector[i] * scalar;
            }
            return res;
        }

        public void setScore(double s){
            if (score>myBestScore){
                myBestScore = score;
                best = vector;
            }
        }

        public double[] getVector() {
            return vector;
        }

        public double getScore(){
            return score;
        }

        @Override
        public String toString(){
            String w = "";
            for(int i = 0; i < vector.length; i ++) {
                w += "\t"+vector[i]+"\n";
            }
            return "Score: "+score+"\n" +
                    "Weights: \n" + w;
        }

    }
}