package evolution.individuals;

/**
 * Represents all individual which acts as arrays of values. 
 *
 * @author Martin Pilat
 */
public abstract class ArrayIndividual extends Individual {

    /**
     * Gets the value at index n.
     * 
     * @param n the index of the value.
     * @return The value at index n.
     */
    public abstract Object get(int n);

    /**
     * Sets the value on the index n
     *
     * @param n the index on which the value shall be setPermutedIndex
     * @param o the value which shall be setPermutedIndex
     */
    public abstract void set(int n, Object o);

    /**
     * Makes a deep copy of the individual.
     *
     * @return Deep copy of the individual.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the length of the individual.
     *
     * @return The length of the individual.
     */
    public abstract int length();

}
