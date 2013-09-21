package tetris;

import java.util.Random;

/**
 *
 * @author Jesse Bordoe
 */
public class GA {

    private static final Random rand = new Random();
    /** probability of mutation */
    private static final double mutationRate = 0.08;
    private static final Game tetris = new Game(false);
    private static final int runs = 20;
    private static final int popSize = 100;
    private static final int testRuns = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args){
        GA evolver = new GA();
        evolver.evolve();
    }

    public static void evolve(){
        Candidate[] population = new Candidate[popSize];
        Candidate champion = null;
        double total = 0;
        //initialize population
        System.out.println("initializing population...");
        for(int i = 0; i < popSize; i ++){
            double[] weights = new double[12];
            for(int j = 0; j < 4; j ++) {
               weights[j] = 1-(rand.nextDouble()*2);
            }
            population[i] = new Candidate(weights);
            double score = tetris.runAI(testRuns, weights);
            total+=score;
            population[i].setScore(score);
        }
        champion = getBest(population);
        System.out.println("best score: "+(int)champion.getScore());
        for(int i = 0; i < runs; i ++){
            Candidate best = null;
            long stamp = System.currentTimeMillis();
            double mean = 0;
            System.out.println("run-"+(i+1));
            population = roulette(population,total);
            total = 0;
            for(int j = 0; j < popSize; j ++){
                double score = tetris.runAI(testRuns, population[j].getWeights());
                mean+=score;
                total+=score;
                population[j].setScore(score);
            }
            best = getBest(population);
            System.out.println("Best: "+(int)best.getScore()+
                    ", mean: "+mean/popSize+
                    ", time: "+(System.currentTimeMillis()-stamp)/1000+"s");
            if(best.getScore() > champion.getScore()){
                champion = best;
            }
        }
        System.out.println(champion.toString());
    }
    private static Candidate[] tournament(Candidate[] pop){
        Candidate[] newPop = new Candidate[popSize];
        Candidate[] parents = new Candidate[popSize/2];
        //select parents
        for(int i = 0; i < popSize/2; i++){
            Candidate p1 = pop[rand.nextInt(popSize)];
            Candidate p2 = pop[rand.nextInt(popSize)];
            double val = rand.nextDouble()*(p1.getScore()+p2.getScore());
            parents[i] = val > p1.getScore()? p2 : p1;
        }
        for(int i = 0; i < popSize/2; i++){
            Candidate[] kids = crossover(parents[i],parents[rand.nextInt(popSize/2)]);
            newPop[i*2] = kids[0];
            newPop[(i*2)+1] = kids[1];
        }
        return newPop;
    }

    private static Candidate[] roulette(Candidate[] pop, double total) {
        int selected = 0;
        Candidate[] newPop = new Candidate[popSize];
        Candidate[] pool = new Candidate[popSize / 2];
        //select parents
        while (selected < pool.length) {
            double threshold = rand.nextDouble(); // our roulette 'pointer'
            int cand = 0;
            double temp = 0;
            while (true) { // find the correct pocket
                double prob = pop[cand].getScore();
                temp += prob / total;
                if (temp >= threshold) { // when we land on pocket...
                    pool[selected] = pop[cand]; //...add candidate to mating pool
                    selected++;
                    break;
                }
                cand++;
            }
        }
        //perform crossover
        for (int i = 0; i < popSize / 2; i++) {
            Candidate[] kids = crossover(pool[i], pool[rand.nextInt(popSize / 2)]);
            newPop[i * 2] = kids[0];
            newPop[(i * 2) + 1] = kids[1];
        }
        return newPop;
    }

    private static double getMean(Candidate[] pop) {
        double mean = 0.0;
        for(Candidate c: pop){
            mean += c.getScore();
        }
        return mean/popSize;
    }

    private static Candidate getBest(Candidate[] pop) {
        double best = 0.0;
        Candidate winner = null;
        for(Candidate c: pop){
            if(c.getScore()>best){
                best = c.getScore();
                winner = c;
            }
        }
        return winner;
    }

    private static Candidate[] crossover(Candidate p1, Candidate p2) {
        Candidate[] children = new Candidate[2];
        double[] w1 = p1.getWeights().clone();
        double[] w2 = p2.getWeights().clone();

        for(int i = 0; i < w1.length; i++ ){
            if(rand.nextDouble()>0.5){
                double temp = w1[i];
                w1[i] = w2[i];
                w2[i] = temp;
            }
        }
        Candidate c1 = new Candidate(w1);
        Candidate c2 = new Candidate(w2);
        mutate(c1);
        mutate(c2);
        return new Candidate[]{c1,c2};
    }

    private static void mutate(Candidate c){
        double[] w = c.getWeights();
        for(int i = 0; i < w.length; i++) {
            if(rand.nextDouble() < mutationRate){
                w[i]+= (0.5-rand.nextDouble())*0.4;
                if(w[i] > 1){ w[i] = 1;} else if(w[i]<-1){w[i]=-1;}
            }
        }
    }

    private static class Candidate {
        double[] weights;
        double score;

        public Candidate(double[] weights) {
            this.weights = weights;
        }

        public void setScore(double s){
            this.score = s;
        }

        public double[] getWeights() {
            return weights;
        }

        public double getScore(){
            return score;
        }

        @Override
        public String toString(){
            String w = "";
            for(int i = 0; i < weights.length; i ++) {
                w += "\t"+weights[i]+"\n";
            }
            return "Score: "+score+"\n" +
                    "Weights: \n" + w;
        }

    }

}
