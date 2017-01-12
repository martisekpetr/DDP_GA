package evolution.doubledigest;

import evolution.FitnessFunction;
import evolution.individuals.DigestIndividual;
import evolution.individuals.Individual;

import java.util.Arrays;

public class DigestFitness implements FitnessFunction {

    // fitness function must know the segments sizes, not only their permutation

    // fragment lengths
    private int[] a;
    private int[] ab;
    private int[] b;

    public DigestFitness(int[]a, int[]b, int[] ab) {
        this.a = a;
        this.b = b;
        this.ab = ab;
    }

    @Override
    public double evaluate(Individual individual) {

        DigestIndividual digestIndividual = (DigestIndividual) individual;

        double length = 0;

        // construct the physical map: place the cuts made by the enzyme A according to the permutation
        int[] cutsA = new int[digestIndividual.countA()+1];
        cutsA[0] = 0;
        int pos = 0;
        for (int i = 0; i < digestIndividual.countA(); i++) {
            int index = (Integer)digestIndividual.getPermutedIndex(0,i);
            pos += a[index];
            cutsA[i+1] = pos;
        }

        // construct the physical map: place the cuts made by the enzyme B according to the permutation
        int[] cutsB = new int[digestIndividual.countB()+1];
        cutsB[0] = 0;
        pos = 0;
        for (int i = 0; i < digestIndividual.countB(); i++) {
            pos += b[(Integer)digestIndividual.getPermutedIndex(1,i)];
            cutsB[i+1] = pos;
        }

        // construct the physical map: place the cuts made by the enzymes A and B according to the permutation
        int[] cutsAB = new int[cutsA.length + cutsB.length];

        int indexA = 0;
        int indexB = 0;
        int indexAB = 0;

        // merge the two physical maps
        while (indexA < cutsA.length && indexB < cutsB.length){
            if(cutsA[indexA] == cutsB[indexB]){
                cutsAB[indexAB] = cutsA[indexA];
                indexA++;
                indexB++;
            } else if(cutsA[indexA] < cutsB[indexB]){
                cutsAB[indexAB] = cutsA[indexA];
                indexA++;
            } else {
                cutsAB[indexAB] = cutsB[indexB];
                indexB++;
            }
            indexAB++;
        }
        while(indexA < cutsA.length){
            cutsAB[indexAB] = cutsA[indexA];
            indexA++;
            indexAB++;
        }
        while(indexB < cutsB.length){
            cutsAB[indexAB] = cutsB[indexB];
            indexB++;
            indexAB++;
        }


        // get the AB fragments
        int[] computedAB = new int[indexAB-1];

        for(int i = 1; i < indexAB; i++){
            computedAB[i-1] = cutsAB[i] - cutsAB[i-1];
        }

        // compare the resulting mapping with the original
        Arrays.sort(computedAB);
        Arrays.sort(ab);


        /*********************************************
         * distance measure is the number of misfits
         * room for improvement
         */
        int distance = 0;
        int i = 0;
        int j = 0;
        while (i < computedAB.length && j < ab.length){
            if(computedAB[i] == ab[j]){
                i++;
                j++;
            } else if(computedAB[i] < ab[j]){
                distance++;
                i++;
            } else {
                distance++;
                j++;
            }
        }
        while(i < cutsA.length){
            distance++;
            i++;
        }
        while(j < cutsB.length){
            distance++;
            j++;
        }
/*
        int distance = 0;
        int i = 0;
        int j = 0;
        while (i < computedAB.length && j < ab.length){
            if(computedAB[i] == ab[j]){
                i++;
                j++;
            } else if(computedAB[i] < ab[j]){
                distance += ((computedAB[i]-ab[j])*(computedAB[i]-ab[j]))/ab[j];
                i++;
            } else {
                distance += ((computedAB[i]-ab[j])*(computedAB[i]-ab[j]))/ab[j];
                j++;
            }
        }*/
        /*
        while(i < cutsA.length){
            distance += ((computedAB[i]-ab[j])*(computedAB[i]-ab[j]))/ab[j];
            i++;
        }
        while(j < cutsB.length){
            distance++;
            j++;
        }*/

        digestIndividual.setObjectiveValue(distance);

        if(distance == 0){
            // TODO found the correct mapping, stop the evolution?
        }

        return 1 / (float)distance;
    }
}
