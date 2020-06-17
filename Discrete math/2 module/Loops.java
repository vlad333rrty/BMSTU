import java.util.*;

public class Loops{
    private static Vertex[] vertex;
    private static Map<Integer,Integer> map=new HashMap<>();
    private static Scanner scan=new Scanner(System.in);
    private static ArrayList<Integer> order=new ArrayList<>();
    public static void main(String[] args){
        int n=scan.nextInt();
        vertex=new Vertex[n];
        read(n);
        DFS();
        dominators();
        for (int i:order)
            vertex[i].mark=false;
        System.out.println(cycles());
    }
    private static boolean Find_C(Vertex v,Vertex aim){
        try {
            if (v.dom == aim)
                return true;
            return Find_C(v.dom, aim);
        }catch (NullPointerException e){
            return false;
        }
    }
    private static void read(int n){
        boolean flag=true;
        int l;
        for (int i=0;i<n;i++){
            if (flag) {
                l = scan.nextInt();
                vertex[i] = new Vertex(l);
                map.put(l, i);
            }
            String s=scan.next();
            if (s.equals("JUMP")){
                l=scan.nextInt();
                vertex[i].getList().add(l);
                flag=true;
            }else{
                if (s.equals("BRANCH")){
                    l=scan.nextInt();
                    vertex[i].getList().add(l);
                }
                if (i+1>=n) break;
                l=scan.nextInt();
                vertex[i].getList().add(l);
                map.put(l,i+1);
                vertex[i+1]=new Vertex(l);
                flag=false;
            }
        }
    }
    private static int cycles(){
        int c=0;
        for (int i:order){
            for (int j:vertex[i].parents){
                if (Find_C(vertex[j],vertex[i])) {
                    c++;
                    break;
                }
            }
        }
        return c;
    }
    private static Vertex FindMin(Vertex v){
        if (v.ancestor==null)
            return v;
        else{
            Stack<Vertex> s=new Stack<>();
            Vertex u=v;
            while (u.ancestor.ancestor!=null){
                s.push(u);
                u=u.ancestor;
            }
            while (!s.isEmpty()){
                v=s.pop();
                if (v.ancestor.Label.sdom.time<v.Label.sdom.time)
                    v.Label=v.ancestor.Label;
                v.ancestor=u.ancestor;
            }
            return v.Label;
        }
    }
    private static void DFS(){
        Stack<Integer> s=new Stack<>();
        vertex[0].mark=true;
        s.add(0);
        int t=1;
        while(!s.isEmpty()){
            int u=s.pop();
            order.add(u);
            for (int i=0;i<vertex[u].getList().size();i++){
                int next=map.get(vertex[u].getList().get(i));
                vertex[next].parents.add(u);
                if (!vertex[next].mark) {
                    vertex[next].parent=vertex[u];
                    vertex[next].mark = true;
                    vertex[next].time=t++;
                    s.add(next);
                }
            }
        }
    }
    private static void dominators(){
        for (int i=order.size()-1;i>0;i--){
            int ind=order.get(i);
            for (int j:vertex[ind].parents){
                Vertex u=FindMin(vertex[j]);
                if (u.sdom.time<vertex[ind].sdom.time)
                    vertex[ind].sdom=u.sdom;
            }
            vertex[ind].ancestor=vertex[ind].parent;
            vertex[ind].sdom.bucket.add(vertex[ind]);
            for (Vertex v:vertex[ind].parent.bucket){
                Vertex u=FindMin(v);
                v.dom=(u.sdom==v.sdom ? v.sdom : u);
            }
            vertex[ind].parent.bucket.clear();
        }
        int n=order.size();
        for (int i=1;i<n;i++){
            int ind=order.get(i);
            if (vertex[ind].dom!=vertex[ind].sdom)
                vertex[ind].dom=vertex[ind].dom.dom;
        }
        vertex[0].dom=null;
    }
}
class Vertex{
    private ArrayList<Integer> list;
    int label;
    Vertex ancestor,parent;
    Vertex Label;
    Vertex sdom,dom;
    int time;
    boolean mark=false;
    ArrayList<Integer> parents;
    ArrayList<Vertex> bucket;
    Vertex(int l){
        label=l;
        parents=new ArrayList<>();
        sdom=Label=this;
        ancestor=null;
        bucket=new ArrayList<>();
        list=new ArrayList<>();
    }
    ArrayList<Integer> getList(){
        return list;
    }
}

