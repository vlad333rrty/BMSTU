import java.util.*;
public class MaxComponent {
    public static void main(String[] args) {
        Scanner scan = new Scanner (System.in);
        int n=scan.nextInt();
        int edges=scan.nextInt();
        if (edges==0){
            StringBuilder fin=new StringBuilder("graph{\n0 [ color = red ]\n");
            for (int i=1;i<n;i++)
                fin.append(i+"\n");
            System.out.println(fin.append("}"));
            return;
        }
        boolean[] v=new boolean[n];
        Vertex[] vertex=new Vertex[n];
        for (int i=0;i<edges;i++){
            int a=scan.nextInt();
            int b=scan.nextInt();
            if (!v[a])
                vertex[a]=new Vertex();
            v[a]=true;
            Edge e1=new Edge(a,b);
            Edge e2=new Edge(b,a);
            e1.setTwin(e2);
            e2.setTwin(e1);
            vertex[a].getList().add(e1);
            if (!v[b])
                vertex[b]=new Vertex();
            v[b]=true;
            vertex[b].getList().add(e2);
        }
        Thread t=new Thread(null,null,"Main",2<<23){
            public void run(){
                StringBuilder fin=new StringBuilder("graph{\n");
                ArrayList<Vertex> prq=DFS(vertex,v,fin);
                Collections.sort(prq);
                for (int i=0;i<prq.size();i++)
                    if (i!=prq.size()-1)
                        help(vertex,prq.get(i).getList().get(0).getFrom(),"",fin);
                    else
                        help(vertex,prq.get(i).getList().get(0).getFrom()," [ color = red ]",fin);
                System.out.println(fin.append("}\n"));
            }
        };
        t.start();


    }
    public static void help(Vertex[] vertex,int v,String s,StringBuilder fin){
        if (!vertex[v].isPrinted()){
            vertex[v].setPrinted(true);
            fin.append(v+s+"\n");
        }
        for (int i=0;i<vertex[v].getList().size();i++){
            Edge next=vertex[v].getList().get(i);
            if (!next.isPrinted()){
                next.setPrinted(true);
                next.getTwin().setPrinted(true);
                fin.append(v+"--"+next.getTo()+s+"\n");
                help(vertex,next.getTo(),s,fin);
            }
        }

    }
    public static ArrayList<Vertex> DFS(Vertex[] vertex,boolean[] v,StringBuilder fin){
        ArrayList<Vertex> prq=new ArrayList<>();
        for (int i=0,n=0;i<vertex.length;i++) {
            if (!v[i]){
                fin.append(i+"\n");
                continue;
            }
            if (!vertex[i].isMark()) {
                visitVertex(vertex, i, new Comp(n++));
                prq.add(vertex[i]);
            }
        }
        return prq;
    }
    public static void visitVertex(Vertex[] vertex,int v,Comp comp){
        if (!vertex[v].isMark()) {
            vertex[v].setMark(true);
            vertex[v].setComp(comp);
            vertex[v].getComp().inc();
        }
        if (v<vertex[v].getComp().getMin())
            vertex[v].getComp().setMin(v);
        for (int i=0;i<vertex[v].getList().size();i++){
            Edge next=vertex[v].getList().get(i);
            if (!next.isMark()) {
                next.setMark(true);
                next.getTwin().setMark(true);
                vertex[v].getComp().incEdges();
                visitVertex(vertex, next.getTo(), comp);
            }
        }
    }
}
class Vertex implements  Comparable<Vertex>{
    private ArrayList<Edge> list;
    private Comp comp;
    private boolean mark;
    private boolean printed;
    public  Vertex(){
        list=new ArrayList<>();
        mark=false;
        printed=false;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public ArrayList<Edge> getList() {
        return list;
    }

    public void setComp(Comp comp) {
        this.comp = comp;
    }

    public Comp getComp() {
        return comp;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    @Override
    public int compareTo(Vertex o) {
        if (this.getComp().getCount()!=o.getComp().getCount()) {
            return this.getComp().getCount() - o.getComp().getCount();
        }
        if (this.getComp().getEdges()!=o.getComp().getEdges()) {
            return this.getComp().getEdges() - o.getComp().getEdges();
        }
        return -this.getComp().getMin()+o.getComp().getMin();
    }
}
class Comp{
    private int number;
    private int count;
    private int min;
    private  int edges;
    public Comp(int n){
        min=Integer.MAX_VALUE;
        number=n;
        edges=0;
    }
    public int getNumber() {
        return number;
    }


    public int getCount() {
        return count;
    }

    public void inc(){
        count++;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {
        return min;
    }
    public void incEdges(){
        edges++;
    }

    public int getEdges() {
        return edges;
    }
}
class Edge{
    private boolean mark;
    private boolean printed;
    private int from;
    private int to;
    private Edge twin;
    public Edge(int a,int b){
        from=a;
        to=b;
        mark=false;
        printed=false;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public boolean isMark() {
        return mark;
    }

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public boolean isPrinted() {
        return printed;
    }

    public Edge getTwin() {
        return twin;
    }

    public void setTwin(Edge twin) {
        this.twin = twin;
    }
}
