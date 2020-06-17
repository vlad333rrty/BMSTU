import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

public class SparseSet<T extends Hintable> extends AbstractSet<T> {
    private final ArrayList<T> dense=new ArrayList<>();
    private int n;
    
    @Override
    public Iterator<T> iterator() {
        return new Iter();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        T val=(T)o;
        return val.hint()>=0 && val.hint()<n && dense.get(val.hint())==val;
    }

    @Override
    public boolean add(T val) {
        if (val.hint()>= n || dense.get(val.hint())!=val){
            dense.add(val);
            val.setHint(n);
            n++;
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        T val=(T) o;
        if (contains(val)){
            dense.get(n-1).setHint(val.hint());
            dense.set(val.hint(),dense.get(n-1));
            n--;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        n =0;
    }

    private class Iter implements Iterator<T>{
        private int ind;
        public boolean hasNext() {
            return ind < n;
        }
        public T next() {
            return dense.get(ind++);
        }

        public void remove() {
            SparseSet.this.remove(dense.get(ind-1));
        }
    }

    @Override
    public int size() {
        return n;
    }
}

