package pt.tecnico.sauron.silo.domain;

import java.util.*;
import pt.tecnico.sauron.silo.domain.*;
import java.util.stream.Collectors;
import java.util.stream.*; 

public class VectorClock {
    List<Integer> _vectorClock = new ArrayList<>();

    public VectorClock(int i) {
        for(int j = 0; j < i; j++){
            //initializing all the values in the vector clock to zero
            _vectorClock.add(0);
        }
    }

    /**
     * Gets lists corresponding to vector clock
     * @return List<Integer>
     */
    public List<Integer> getVectorClock() {
        return this._vectorClock;
    }

    /**
     * Sets vector clock list
     * @param vectorClock
     */
    public void setVectorClock(List<Integer> vectorClock) {
        this._vectorClock = vectorClock;
    }

    /**
     * Increment a specific position of the vector clock
     * @param position
     */
    public synchronized void incrementPositionVectorClock(int position) {
        this._vectorClock.set(position - 1, 1 + this._vectorClock.get(position - 1));
    }

    /**
     * Compare vector clock
     * @param vc
     * @param i
     * @return int
     */
    public synchronized int compareVectorClock(VectorClock vc, int i) {
        return this.getPositionValueVectorClock(i) - vc.getPositionValueVectorClock(i);
    }

    /**
     * Get position of a vector clock
     * @param position
     * @return int
     */
    protected synchronized int getPositionValueVectorClock(int position) {
        return this._vectorClock.get(position - 1);
    }

    /**
     *  Print the vector clock
     */
    public void printVector() { for (int i :_vectorClock) System.out.print(i + " "); System.out.print("\n"); }

    /**
     * Reset the vector clock to set everything to zero
     */
    public void resetVector() {
        for (int i = 0; i < _vectorClock.size(); i++){
            _vectorClock.set(i, 0);
        }
    }
}