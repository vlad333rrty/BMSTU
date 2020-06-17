import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Kruskal {
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int n=scanner.nextInt();
        Vertex[] vertices=IntStream.range(0,n).mapToObj(i->new Vertex(scanner.nextInt(), scanner.nextInt())).toArray(Vertex[]::new);
        PriorityQueue<Edge> prq=new PriorityQueue<>();
        for (int i=0;i<n;i++){
            for (int j=i+1;j<n;j++){
                prq.add(new Edge(vertices[i], vertices[j]));
            }
        }
        double res=0.0;
        for (int i=0;i<n-1 && !prq.isEmpty();){
            Edge e=prq.poll();
            if (find(e.from)!=find(e.to)){
                res+=e.len;
                i++;
                union(e.from,e.to);
            }
        }
        System.out.printf("%.2f",res);
    }

    private static void union(Vertex x, Vertex y){
        Vertex rootX=find(x);
        Vertex rootY=find(y);
        if (rootX.depth<rootY.depth){
            rootX.parent=rootY;
        }else{
            rootY.parent=rootX;
            if (rootX!=rootY && rootX.depth==rootY.depth){
                rootX.depth++;
            }
        }
    }
    private static Vertex find(Vertex v){
        if (v.parent==v){
            return v;
        }else {
            v.parent=find(v.parent);
            return v.parent;
        }
    }
}

class Vertex {
    private int x,y;
    int depth;
    Vertex parent;
    Vertex(int x, int y){
        this.x=x;
        this.y=y;
        parent=this;
    }
    double distance(Vertex n){
        return Math.sqrt((x-n.x)*(x-n.x)+(y-n.y)*(y-n.y));
    }
}

class Edge implements Comparable<Edge>{
    double len;
    Vertex from,to;
    Edge(Vertex f, Vertex t){
        len=f.distance(t);
        from=f;
        to=t;
    }

    @Override
    public int compareTo(Edge o) {
        return Double.compare(len, o.len);
    }
}

