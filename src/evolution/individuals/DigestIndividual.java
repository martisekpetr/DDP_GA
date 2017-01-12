package evolution.individuals;

import evolution.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Individual for the DDP is a pair of permutations, one for each set of fragments (given by enzymes A and B). Fitness
 * is evaluated by comparing the resulting physical map with a set of fragments given by the combined use of enzymes A and B.
 */
public class DigestIndividual extends Individual {

    // order of fragments
    private int[] permA = null;
    private int[] permB = null;

    public DigestIndividual(int countA, int countB) {
        permA = new int[countA];
        permB = new int[countB];
    }

    public String toString() {
        return Arrays.toString(permA) + "; " + Arrays.toString(permB);
    }

    public int[] toIntArrayA() {
        return permA;
    }
    public int[] toIntArrayB() {
        return permB;
    }

    public Object getPermutedIndex(int enzyme, int n) {
        if(enzyme == 0){
            return permA[n];
        } else if (enzyme == 1){
            return permB[n];
        }
        else return null;
    }

    public void setPermutedIndex(int enzyme, int n, Object o) {
        if(enzyme == 0){
            permA[n] = (Integer)o;
        } else if (enzyme == 1){
            permB[n] = (Integer)o;
        }
    }

    /**
     * Randomly initializes the individual: generates two random permutations.
     */
    @Override
    public void randomInitialization() {
        // tohle je hnusarna, ale zjevne bych to musel mit jako Integer[] misto int[], aby to slo jednoduseji...
        ArrayList<Integer> randA = new ArrayList<>(permA.length);
        ArrayList<Integer> randB = new ArrayList<>(permB.length);

        for (int j = 0; j < permA.length; j++) {
            randA.add(j);
        }
        for (int j = 0; j < permB.length; j++) {
            randB.add(j);
        }

        Collections.shuffle(randA, RandomNumberGenerator.getInstance().getRandom());
        Collections.shuffle(randB, RandomNumberGenerator.getInstance().getRandom());

        for (int j = 0; j < countA(); j++) {
            permA[j] = randA.get(j);
        }
        for (int j = 0; j < countB(); j++) {
            permB[j] = randB.get(j);
        }
    }

    @Override
    public Object clone() {

        DigestIndividual newDI = (DigestIndividual) super.clone();
        newDI.permA = new int[permA.length];
        System.arraycopy(permA, 0, newDI.permA, 0, permA.length);
        newDI.permB = new int[permB.length];
        System.arraycopy(permB, 0, newDI.permB, 0, permB.length);
        return newDI;
    }

    public int countA() {
        return permA.length;
    }
    public int countB() {
        return permB.length;
    }
    public int count(int enzyme) {
        if (enzyme == 0) return permA.length;
        if (enzyme == 1) return permB.length;
        else return 0;
    }

}
