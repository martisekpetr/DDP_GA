package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.DigestIndividual;

public class DigestInversionMutation implements Operator {

    double mutationProbability;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    public DigestInversionMutation(double mutationProbability) {
        this.mutationProbability = mutationProbability;

    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            DigestIndividual p1 = (DigestIndividual) parents.get(i);
            DigestIndividual o1 = (DigestIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                int pt1 = rng.nextInt(p1.countA());
                int pt2 = rng.nextInt(p1.countA());
                int start = Math.min(pt1, pt2);
                int end = Math.max(pt1,pt2);
                for(int k = start; k < end; k++){
                    o1.setPermutatedIndex(0, k, p1.getPermutatedIndex(0, end-k+start-1));
                }
            }
            if (rng.nextDouble() < mutationProbability) {
                int pt1 = rng.nextInt(p1.countB());
                int pt2 = rng.nextInt(p1.countB());
                int start = Math.min(pt1, pt2);
                int end = Math.max(pt1,pt2);
                for(int k = start; k < end; k++){
                    o1.setPermutatedIndex(1, k, p1.getPermutatedIndex(1, end-k+start-1));
                }
            }
            offspring.add(o1);
        }
    }

}
