package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;
import evolution.individuals.DigestIndividual;

/**
 * A mutation which swaps the values on different positions in a single individual.
 * 
 * @author Martin Pilat
 */
public class DigestSwappingMutation implements Operator {

    double mutationProbability;
    double mutProbPerEnzyme;
    double geneChangePercentage;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();


    public DigestSwappingMutation(double mutationProbability, double mutProbPerEnzyme, double geneChangePercentage) {
        this.mutationProbability = mutationProbability;
        this.mutProbPerEnzyme = mutProbPerEnzyme;
        this.geneChangePercentage = geneChangePercentage;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            DigestIndividual p1 = (DigestIndividual) parents.get(i);
            DigestIndividual o1 = (DigestIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                // enzyme A
                if (rng.nextDouble() < mutProbPerEnzyme) {
                    for (int j = 0; j < geneChangePercentage * p1.countA(); j++) {
                        int pt1 = rng.nextInt(p1.countA());
                        int pt2 = rng.nextInt(p1.countA());
                        Object v1 = o1.getPermutedIndex(0, pt1);
                        Object v2 = o1.getPermutedIndex(0, pt2);

                        o1.setPermutedIndex(0, pt1, v2);
                        o1.setPermutedIndex(0, pt2, v1);
                    }
                }

                // enzyme B
                if (rng.nextDouble() < mutProbPerEnzyme) {
                    for (int j = 0; j < geneChangePercentage * p1.countB(); j++) {
                        int pt1 = rng.nextInt(p1.countB());
                        int pt2 = rng.nextInt(p1.countB());
                        Object v1 = o1.getPermutedIndex(1, pt1);
                        Object v2 = o1.getPermutedIndex(1, pt2);

                        o1.setPermutedIndex(1, pt1, v2);
                        o1.setPermutedIndex(1, pt2, v1);
                    }
                }
            }

            offspring.add(o1);
        }
    }
}
