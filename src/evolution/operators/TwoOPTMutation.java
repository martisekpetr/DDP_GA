package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.IntegerIndividual;
import evolution.tsp.Coordinates;
import evolution.tsp.TSPFitness;

import java.util.Vector;

public class TwoOPTMutation implements Operator {

    double mutationProbability;
    Vector<Coordinates> coords;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    public TwoOPTMutation(double mutationProbability, Vector<Coordinates> coords) {
        this.mutationProbability = mutationProbability;
        this.coords = coords;

    }
    private boolean contains(IntegerIndividual ind, int start, int end, int value){
        for(int i = start; i < end; i++){
            if((Integer)ind.get(i) == value){
                return true;
            }
        }
        return false;
    }
    private boolean isPermutation(IntegerIndividual ind){
        for(int i = 0; i < ind.length(); i++){
            if(!contains(ind, 0, ind.length(), i)){
                return false;
            }
        }
        return true;
    }

    private double getLength(IntegerIndividual ind){
        double length = 0;
        for (int k = 0; k < ind.length() - 1; k++) {
            int s = (Integer) ind.get(k);
            int e = (Integer) ind.get(k + 1);
            length += Coordinates.distance(coords.get(s), coords.get(e));
        }

        length += Coordinates.distance(coords.get((Integer) ind.get(ind.length() - 1)), coords.get((Integer) ind.get(0)));
        return length;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            IntegerIndividual p1 = (IntegerIndividual) parents.get(i);
            IntegerIndividual o1 = (IntegerIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                int pt1 = rng.nextInt(p1.length());
                int pt2 = rng.nextInt(p1.length());
                int start = Math.min(pt1, pt2);
                int end = Math.max(pt1,pt2);
                for(int k = start; k < end; k++){
                    o1.set(k, p1.get(end-k+start-1));
                }

                if(getLength(o1) < getLength(p1)){
                    offspring.add(o1);
                } else{
                    offspring.add(p1);
                }


            }
            offspring.add(o1);
        }
    }

}
