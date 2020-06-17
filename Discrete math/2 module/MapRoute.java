import java.util.*;
import java.io.DataInputStream;
import java.io.InputStream;
public class MapRoute {
    private static int[] dist;
    private static Vertex[] vertex;
    private static int[] map;
    public static void main(String[] args) {
        Parser p=new Parser(System.in);
        int n = p.nextInt();
        int N=n*n;
        dist = new int[N];
        vertex=new Vertex[N];
        map = new int[N];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                map[i*n+j] = p.nextInt();
                dist[j+n*i]=Integer.MAX_VALUE;
                vertex[j+n*i]=new Vertex();
                if (i + 1 < n ) {
                    vertex[j+n*i].getList().add(new Coordinates(i+1,j));
                }
                if (j+1<n){
                    vertex[j+n*i].getList().add(new Coordinates(i,j+1));
                }
                if (i-1>=0){
                    vertex[j+n*i].getList().add(new Coordinates(i-1,j));
                }
                if (j-1>=0){
                    vertex[j+n*i].getList().add(new Coordinates(i,j-1));
                }
            }
        }
        dijkstra(n);
        System.out.println(dist[N-1]+map[0]);
    }
    private static void dijkstra(int k){
        Queue<Int> prq=new PriorityQueue<>();
        dist[0]=0;
        for (int i=0;i<vertex.length;i++)
            prq.add(new Int(i,dist[i]));
        while(!prq.isEmpty()){
            Int v=prq.poll();
            vertex[v.getValue()].setMark(true);
            for (int i=0;i<vertex[v.getValue()].getList().size();i++){
                Coordinates to=vertex[v.getValue()].getList().get(i);
                int ind=to.getX()*k+to.getY();
                if (!vertex[ind].isMark() && Relax(v.getValue(),ind,map[ind]))
                    prq.add(new Int(ind,dist[ind]));
            }
        }
    }
    private static boolean Relax(int u,int v,int w){
        boolean changed=dist[u]+w<dist[v];
        if (changed){
            dist[v]=dist[u]+w;
        }
        return changed;
    }

}
class Vertex{
    private ArrayList<Coordinates> list;
    private boolean mark;
    Vertex(){
        list=new ArrayList<>();
    }
    void setMark(boolean mark){
        this.mark=mark;
    }
    boolean isMark(){
        return mark;
    }
    ArrayList<Coordinates> getList(){
        return list;
    }

}
class Coordinates{
    private int x;
    private int y;
    Coordinates(int x,int y){
        this.x=x;
        this.y=y;
    }
    int getX(){
        return x;
    }
    int getY(){
        return y;
    }
}
class Int implements Comparable<Int>{
    private int value;
    private int key;
    Int(int n,int k){
        value=n;
        key=k;
    }
    int getValue(){
        return value;
    }
    @Override
    public int compareTo(Int o) {
        return key-o.key;
    }
}

//быстрый ввод
class Parser {
    final private int BUFFER_SIZE = 1 << 16;
    private DataInputStream din;
    private byte[] buffer;
    private int bufferPointer, bytesRead;

    public Parser(InputStream in) {
        din = new DataInputStream(in);
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = bytesRead =  0;
    }
    int nextInt() {
        int ret =  0;
        boolean neg;
        try {
            byte c = read();
            while (c <= ' ')
                c = read();
            neg = c == '-';
            if (neg)
                c = read();
            do {
                ret = ret * 10 + c - '0';
                c = read();
            } while (c > ' ');

            if (neg) return -ret;
        } catch (Exception e) {}
        return ret;
    }
    private void fillBuffer() {
        try {
            bytesRead = din.read(buffer, bufferPointer =  0, BUFFER_SIZE);
        } catch (Exception e) {}
        if (bytesRead == -1) buffer[ 0] = -1;
    }

    private byte read() {
        if (bufferPointer == bytesRead) fillBuffer();
        return buffer[bufferPointer++];
    }
}
