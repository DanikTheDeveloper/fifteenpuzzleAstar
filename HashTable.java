package fifteenpuzzle;

import java.util.*;

public class HashTable {
    private final Map<Integer, LinkedList<State>> table;
    public HashTable() {
        table = new HashMap<>();
    }
    public boolean offer(State state) {
        Integer hash = state.getBoard().hashCode();
        LinkedList<State> states = table.get(hash);
        if (states == null) {
            states = new LinkedList<>();
            table.put(hash, states);
        }
        if (states.contains(state))
            return false;
        else {
            states.add(state);
            return true;
        }
    }
    public LinkedList<State> get(Integer s) {return table.get(s);}
}
