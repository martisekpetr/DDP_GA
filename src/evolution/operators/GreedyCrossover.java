package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;
import evolution.individuals.IntegerIndividual;
import evolution.tsp.Coordinates;

import java.util.Vector;

public class GreedyCrossover implements Operator {

    double xOverProb = 0;
    Vector<Coordinates> coords;

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probability of crossover
     *
     * @param prob the probability of crossover
     */

    public GreedyCrossover(double prob, Vector<Coordinates> coords) {
        xOverProb = prob;
        this.coords = coords;
    }

    private boolean contains(ArrayIndividual ind, int start, int end, int value){
        for(int i = start; i < end; i++){
            if((Integer)ind.get(i) == value){
                return true;
            }
        }
        return false;
    }

    private int findNext(ArrayIndividual ind, int value){
        for(int i = 0; i < ind.length(); i++){
            if((Integer)ind.get(i) == value){
                if(i+1 >= ind.length()){
                    return (Integer)ind.get(0);
                }
                return (Integer)ind.get(i+1);
            }
        }
        return -1;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size / 2; i++) {
            ArrayIndividual p1 = (ArrayIndividual) parents.get(2*i);
            ArrayIndividual p2 = (ArrayIndividual) parents.get(2*i + 1);

            ArrayIndividual o1 = (ArrayIndividual) p1.clone();
            ArrayIndividual o2 = (ArrayIndividual) p2.clone();

            if (rng.nextDouble() < xOverProb) {
                int k = 0;
                while(k < o1.length()-1){
                    int city = (Integer)o1.get(k);
                    int candidate1 = findNext(p1, city);
                    int candidate2 = findNext(p2, city);
                    if(Coordinates.distance(coords.elementAt(city), coords.elementAt(candidate1))
                        < Coordinates.distance(coords.elementAt(city), coords.elementAt(candidate2)) && !contains(o1,0,k+1,candidate1) ){
                        o1.set(k+1, candidate1);
                    }else if(!contains(o1,0,k+1,candidate2)){
                        o1.set(k+1, candidate2);
                    } else {
                        int pos;
                        do{
                            pos = RandomNumberGenerator.getInstance().nextInt(p1.length());
                        } while(contains(o1,0,k+1,pos));
                        o1.set(k+1, pos);
                    }
                    k++;
                }
                k = 0;
                while(k < o2.length()-1){
                    int city = (Integer)o2.get(k);
                    int candidate1 = findNext(p1, city);
                    int candidate2 = findNext(p2, city);
                    if(Coordinates.distance(coords.elementAt(city), coords.elementAt(candidate1))
                            < Coordinates.distance(coords.elementAt(city), coords.elementAt(candidate2)) && !contains(o2,0,k+1,candidate1) ){
                        o2.set(k+1, candidate1);
                    }else if(!contains(o2,0,k+1,candidate2)){
                        o2.set(k+1, candidate2);
                    } else {
                        int pos;
                        do{
                            pos = RandomNumberGenerator.getInstance().nextInt(p1.length());
                        } while(contains(o2,0,k+1,pos));
                        o2.set(k+1, pos);
                    }
                    k++;
                }
            }


            offspring.add(o1);
            offspring.add(o2);
        }

    }


}


