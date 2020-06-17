import java.util.*;

public class SkipList<K extends Comparable<K>,V> extends AbstractMap<K,V> {
    private SkipList<K,V>[] next;
    private SkipList<K,V> head;
    private K key;
    private V value;
    private int size;

    public SkipList(int levels) {
        next=new SkipList[levels];
        head =this;
    }

    private SkipList<K, V> Succ(SkipList<K,V> x){
        return x.next[0];
    }

    @Override
    public Set<K> keySet() {
        Set<K> ts=new TreeSet<>();
        SkipList<K,V> x=Succ(head);
        while (x!=null){
            ts.add(x.key);
            x=x.next[0];
        }
        return ts;
    }

    @Override
    public V get(Object key) {
        SkipList<K,V> x=lookup((K) key);
        return x==null ? null : x.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return lookup((K)key)!=null;
    }

    private SkipList<K,V> find(int n){
        SkipList<K,V> x=Succ(head);
        for(;x!=null && n>0;n--,x=x.next[0]);
        return x;
    }

    private class Element implements Map.Entry<K,V>{
        private K key;
        private V value;
        public Element(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V val=this.value;
            this.value=value;
            SkipList.this.put(key,value);
            return val;
        }
    }

    private class SkipListEntry extends AbstractSet{
        private ArrayList<Element> entry=new ArrayList<>();
        SkipListEntry(ArrayList<Element> elements){
            entry.addAll(elements);
        }

        @Override
        public Iterator iterator() {
            return new SkipListEntryIterator();
        }

        private class SkipListEntryIterator implements Iterator{
            int ind;
            public boolean hasNext() {
                return ind<size;
            }

            public Object next() {
                return entry.get(ind++);
            }

            public void remove() {
                ind--;
                SkipList.this.remove(find(ind).key);
                entry.remove(ind);
            }
        }

        public int size() {
            return size;
        }
    }


    private SkipList<K,V> initSkipList(K key, V value){
        SkipList<K,V> prev= head;
        SkipList<K,V> elem=new SkipList<>(next.length);
        elem.head =prev;
        elem.key=key;
        elem.value=value;
        ++size;
        return elem;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        ArrayList<Element> elements=new ArrayList();
        for (int i = 0; i< size; i++){
            SkipList<K,V> x= find(i);
            elements.add(new Element(x.key,x.value));
        }
        return new SkipListEntry(elements);
    }

    private void skip(SkipList<K,V>[] p, K k){
        SkipList<K,V> elem= head;
        for (int i=next.length-1;i>=0;i--){
            while (elem.next[i]!=null && (elem.next[i].key).compareTo(k)<0){
                elem = elem.next[i];
            }
            p[i]=elem;
        }
    }

    public V remove(Object key) {
        K k=(K)key;
        if (keySet().contains(k)){
            SkipList<K,V>[] p=new SkipList[next.length];
            skip(p,k);
            SkipList<K,V> x=Succ(p[0]);
            for (int i=0;i<next.length && p[i].next[i]==x;i++){
                p[i].next[i]=x.next[i];
            }
            size--;
            return x.value;
        }
        return null;
    }

    public V put(K key, V value) {
        SkipList<K,V>[] p=new SkipList[next.length];
        skip(p,key);
        SkipList<K,V> lookup=lookup(key);
        if (lookup!=null){
            V res=lookup.value;
            lookup.value=value;
            return res;
        }
        SkipList<K,V> x= initSkipList(key,value);
        for (int i=0,r=(int)((Math.random()*100))*512;i<next.length && r%2==0;i++,r/=2){
            x.next[i]=p[i].next[i];
            p[i].next[i]=x;
        }
        return null;
    }


    private SkipList<K, V> lookup(K k){
        SkipList<K,V>[] p=new SkipList[next.length];
        skip(p,k);
        SkipList<K,V> x=Succ(p[0]);
        return (x!=null && x.key.equals(k)) ? x : null;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }
}
