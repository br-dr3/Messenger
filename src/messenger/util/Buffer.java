package messenger.util;

import java.util.PriorityQueue;
import java.util.Comparator;
import messenger.util.Triplet;

public class Buffer<T> {
    private PriorityQueue<Triplet<Long, Message, String>> storage;
    private final Comparator<Triplet<Long, Message, String>> customComparator = 
            (t1, t2) -> { return t1.getX().intValue() - t2.getX().intValue();};
    
    public Buffer() {
        storage = new PriorityQueue<>(customComparator);
    }
    
    public void addMessage(Message m) {
        addTriplet(new Triplet<>(m.getId(), m.getSender(), m.getReceiver()));
    }
    
    public void addTriplet(Triplet triplet) {
        this.storage.add(triplet);
    }
}
