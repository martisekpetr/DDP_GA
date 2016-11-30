package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;
import evolution.individuals.Individual;
import evolution.individuals.IntegerIndividual;

import java.util.Vector;


public class MySwappingMutationOperator implements Operator {

    double mutationProbability;
    double geneChangeProbability;
    Vector<Double> weights;
    int K;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probabilities
     * @param mutationProbability the probability of mutating an individual
     * @param geneChangeProbability the percentage of genes which will be swapped in a mutated individual
     */

    public MySwappingMutationOperator(double mutationProbability, double geneChangeProbability, Vector<Double> weights, int K) {
        this.mutationProbability = mutationProbability;
        this.geneChangeProbability = geneChangeProbability;
        this.weights = weights;
        this.K = K;
    }

    public int[] getBinWeights(Individual individual) {
        IntegerIndividual ind = (IntegerIndividual)individual;
        int[] binWeights = new int[K];
        int[] bins = ind.toIntArray();
        for (int i = 0; i < bins.length; i++) {
            binWeights[bins[i]] += weights.get(i);
        }

        return binWeights;

    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {
            ArrayIndividual p1 = (ArrayIndividual) parents.get(i);
            ArrayIndividual o1 = (ArrayIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                int[] binWeights = getBinWeights(p1);
                int hromadka1, hromadka2, r1, r2;
                double vaha1, vaha2;
                do{
                    r1 = RandomNumberGenerator.getInstance().nextInt(p1.length());

                    r2 = RandomNumberGenerator.getInstance().nextInt(p1.length());

                    hromadka1 = (Integer)o1.get(r1);
                    hromadka2 = (Integer)o1.get(r2);
                    vaha1 = weights.get(r1);
                    vaha2 = weights.get(r2);
                } while ((binWeights[hromadka1] < binWeights[hromadka2]) != (vaha1 < vaha2));
                o1.set(r1, hromadka2);
                o1.set(r2, hromadka1);


            }

            offspring.add(o1);
        }
    }
}
