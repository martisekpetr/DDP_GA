package evolution.operators;

/**
 * Created by David on 4. 1. 2017.
 */

import java.util.Random;
import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.DigestIndividual;


public class PMXover implements Operator {

    double xOverProb = 0;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probability of crossover
     *
     * @param prob the probability of crossover
     */
    public PMXover(double prob) {
        xOverProb = prob;
    }

    public void operate(Population parents, Population offspring) {
        int size = parents.getPopulationSize();

        for (int i = 0; i < size / 2; i++) {
            DigestIndividual p1 = (DigestIndividual) parents.get(2*i);
            DigestIndividual p2 = (DigestIndividual) parents.get(2*i + 1);

            DigestIndividual o1 = (DigestIndividual) p1.clone();
            DigestIndividual o2 = (DigestIndividual) p2.clone();

            int enzyme = rng.nextDouble() < 0.5 ? 0 : 1;

            if (rng.nextDouble() < xOverProb) {

                int randomNumBoundary = (p1.count(enzyme)) - 1;
                int cutPoint1 = rng.nextInt(randomNumBoundary);
                int cutPoint2 = rng.nextInt(randomNumBoundary);
                while(cutPoint1 == cutPoint2){
                    cutPoint2 = rng.nextInt(randomNumBoundary);
                }
                if(cutPoint1 > cutPoint2){
                    int temp = cutPoint1;
                    cutPoint1 = cutPoint2;
                    cutPoint2 = temp;
                }

                int[] parent1 = new int[p1.count(enzyme)];
                int[] parent2 = new int[p2.count(enzyme)];
                int[] child1 = new int[o1.count(enzyme)];
                int[] child2 = new int[o2.count(enzyme)];
                int[] segment1;
                int[] segment2;

                for (int j = 0; j < p1.count(enzyme); j++) {
                    parent1[j] = (int)p1.getPermutedIndex(enzyme, j);
                }
                for (int j = 0; j < p2.count(enzyme); j++) {
                    parent2[j] = (int)p2.getPermutedIndex(enzyme,j);
                }

                int capacity_ofSegments = (cutPoint2 - cutPoint1) + 1;
                segment1 = new int[capacity_ofSegments];
                segment2 = new int[capacity_ofSegments];
                int segment1and2Index = 0;
                for(int index = 0; index < parent1.length; index++){
                    if((index >= cutPoint1) && (index <= cutPoint2)){
                        int x = parent1[index];  int y = parent2[index];
                        segment1[segment1and2Index] = x;
                        segment2[segment1and2Index] = y;
                        segment1and2Index++;
                    }
                }


                // first change
                insert_Segments(child1, segment2, cutPoint1, cutPoint2);

                for(int index = 0; index < child1.length; index++){
                    if((index < cutPoint1) || (index > cutPoint2)){
                        child1[index] = parent1[index];
                    }
                }

                for(int index = 0; index < child1.length; index++){
                    if((index < cutPoint1) || (index > cutPoint2)){
                        while(check_forDuplicates(child1, index)){
                            sort_Duplicates(child1, index, segment1, segment2);
                        }
                    }
                }

                // second change
                insert_Segments(child2, segment1, cutPoint1, cutPoint2);

                for(int index = 0; index < child2.length; index++){
                    if((index < cutPoint1) || (index > cutPoint2)){
                        child2[index] = parent1[index];
                    }
                }

                for(int index = 0; index < child2.length; index++) {
                    if ((index < cutPoint1) || (index > cutPoint2)) {
                        while (check_forDuplicates(child2, index)) {
                            sort_Duplicates(child2, index, segment1, segment2);
                        }
                    }
                }

                for (int j = 0; j < child1.length; j++) {
                    o1.setPermutedIndex(enzyme,j,child1[j]);
                }
                for (int j = 0; j < child2.length; j++) {
                    o1.setPermutedIndex(enzyme,j,child2[j]);
                }


                }
            offspring.add(o1);
            offspring.add(o2);
        }
    }

    // For an Element given by its index check that it doesn't appear twice //
    private boolean check_forDuplicates(int [] offspring, int indexOfElement){
        for(int index = 0; index < offspring.length; index++){
            if((offspring[index] == offspring[indexOfElement]) &&
                    (indexOfElement != index) ){
                return true;
            }
        }
        return false;
    }

    // If Element is Duplicated, replace it by using its mapping //
    private void sort_Duplicates(int [] offspring, int indexOfElement, int[] segment1, int[] segment2){
        for(int index = 0; index < segment1.length; index++){
            if(segment1[index] == offspring[indexOfElement]){
                offspring[indexOfElement] = segment2[index];
            }
            else if(segment2[index] == offspring[indexOfElement]){
                offspring[indexOfElement] = segment1[index];
            }
        }
    }

    private void insert_Segments(int[] offspring, int[] segment, int cutPoint1, int cutPoint2){
        int segmentIndex = 0;
        for(int index = 0; index < offspring.length; index++){
            if((index >= cutPoint1) && (index <= cutPoint2)){
                offspring[index] = segment[segmentIndex];
                segmentIndex++;
            }
        }
    }
}