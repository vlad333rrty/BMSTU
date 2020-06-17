import java.util.*;

public class IntSparseSet extends AbstractSet<Integer>{
    private int[] sparse;
    private int[] dense;
    private int low,high;
    private int ind;
    IntSparseSet(int l,int h){
        low=l;
        high=h;
        sparse=new int[h-l];
        dense=new int[h-l];
        ind=0;
    }
    public Iterator<Integer> iterator() {
        return new Inner();
    }
    public int size() {
        return ind;
    }


    public boolean add(Integer val) {
        int i = val - low;
        if (val<low || val>=high) return false;
        if (sparse[i] >= ind || dense[sparse[i]] != i) {
            sparse[i] = ind;
            dense[ind] = i;
            ind++;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        int del=(Integer) o;
        int i=del-low;
        if (contains(del)) {
            int t = dense[ind - 1];
            dense[sparse[i]] = t;
            sparse[t] = sparse[i];
            ind--;
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        int i=(Integer)o-low;
        return (Integer)o>=low && (Integer)o<high && 0<=sparse[i] && sparse[i]<ind && dense[sparse[i]]==i;
    }

    @Override
    public void clear() {
        ind=0;
    }

    private class Inner implements Iterator<Integer>{
        private int pos;
        public Inner(){
            pos=0;
        }
        public boolean hasNext() {
            return pos<ind;
        }

        @Override
        public Integer next() {
            return dense[pos++]+low;
        }
        public void remove() {
            IntSparseSet.this.remove(dense[pos - 1] + low);
        } 
    }
}
