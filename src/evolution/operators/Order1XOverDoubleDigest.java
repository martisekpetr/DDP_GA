package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.DigestIndividual;


public class Order1XOverDoubleDigest implements Operator {

    double xOverProb = 0;

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probability of crossover
     *
     * @param prob the probability of crossover
     */

    public Order1XOverDoubleDigest(double prob) {
        xOverProb = prob;
    }

    private boolean contains(DigestIndividual ind, int start, int end, int value, int enzyme){
        for(int i = start; i < end; i++){
            if((Integer)ind.getPermutedIndex(enzyme, i) == value){
                return true;
            }
        }
        return false;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size / 2; i++) {
            DigestIndividual p1 = (DigestIndividual) parents.get(2*i);
            DigestIndividual p2 = (DigestIndividual) parents.get(2*i + 1);

            DigestIndividual o1 = (DigestIndividual) p1.clone();
            DigestIndividual o2 = (DigestIndividual) p2.clone();

            if (rng.nextDouble() < xOverProb) {

                int enzyme = rng.nextDouble() < 0.5 ? 0 : 1;

                int pt1 = rng.nextInt(p1.count(enzyme));
                int pt2 = rng.nextInt(p1.count(enzyme));
                int start = Math.min(pt1, pt2);
                int end = Math.max(pt1,pt2);

                int k = 0;
                for (int j = 0; j < start; j++) {
                    int candidate;
                    do{
                        candidate = (Integer)p2.getPermutedIndex(enzyme,k);
                        k++;
                    }
                    while(contains(p1,start,end,candidate,enzyme));
                    o1.setPermutedIndex(enzyme, j, p2.getPermutedIndex(enzyme, k-1));
                }
                for (int j = end; j < p1.count(enzyme); j++) {
                    int candidate;
                    do{
                        candidate = (Integer)p2.getPermutedIndex(enzyme,k);
                        k++;
                    }
                    while(contains(p1,start,end,candidate, enzyme));
                    o1.setPermutedIndex(enzyme, j, p2.getPermutedIndex(enzyme,k-1));
                }

                pt1 = rng.nextInt(p2.count(enzyme));
                pt2 = rng.nextInt(p2.count(enzyme));
                start = Math.min(pt1, pt2);
                end = Math.max(pt1,pt2);

                k = 0;
                for (int j = 0; j < start; j++) {
                    int candidate;
                    do{
                        candidate = (Integer)p1.getPermutedIndex(enzyme,k);
                        k++;
                    }
                    while(contains(p2,start,end,candidate, enzyme));
                    o2.setPermutedIndex(enzyme, j, p1.getPermutedIndex(enzyme,k-1));
                }
                for (int j = end; j < p2.count(enzyme); j++) {
                    int candidate;
                    do{
                        candidate = (Integer)p1.getPermutedIndex(enzyme,k);
                        k++;
                    }
                    while(contains(p2,start,end,candidate,enzyme));
                    o2.setPermutedIndex(enzyme,j, p1.getPermutedIndex(enzyme,k-1));
                }
            }

            offspring.add(o1);
            offspring.add(o2);
        }
    }
}


