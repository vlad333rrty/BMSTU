import java.util.*;
public class Mars{
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        int n=scan.nextInt();
        Vertex[] vertex=new Vertex[n];
        for (int i=0;i<n;i++)
            vertex[i]=new Vertex();
        for (int i=0;i<n;i++)
            for (int j=0;j<n;j++)
                if (scan.next().equals("+")){
                    vertex[i].getList().add(j);
                }
        for (int i=0;i<n;i++)
            if (!vertex[i].isMark())
                if (!BFS(vertex,i))
                    return;
        for (int i=0;i<n;i++)
            vertex[i].setMark(false);
        ArrayList<Part> part=new ArrayList<>();
        for (int i=0;i<n;i++){
            if (!vertex[i].isMark()){
                DFS(vertex,part,i);
            }
        }
        ArrayList<Part> fin=new ArrayList<>();
        cmp(part,fin);
        Collections.sort(fin);
        ArrayList<Integer> out=fin.get(0).getP1();
        Collections.sort(out);
        for (int i:out)
            System.out.print(i+1+" ");
        System.out.println();
    }
    private static boolean BFS(Vertex[] vertex,int v){
        Queue<Integer> q=new ArrayDeque<>();
        q.add(v);
        vertex[v].setTag(true);
        while (!q.isEmpty()){
            int u=q.poll();
            for (int i=0;i<vertex[u].getList().size();i++){
                int next=vertex[u].getList().get(i);
                if (!vertex[next].isMark()){
                    vertex[next].setMark(true);
                    vertex[next].setTag(!vertex[u].isTag());
                    q.add(next);
                }else
                    if (vertex[u].isTag()==vertex[next].isTag()){
                        System.out.print("No solution");
                        return false;
                    }
            }
        }
        return true;
    }
    private static void DFS(Vertex[] vertex,ArrayList<Part> part,int v){
        Stack<Integer> stk=new Stack<>();
        stk.add(v);
        vertex[v].setMark(true);
        Part p=new Part();
        if (vertex[v].isTag())
            p.getP1().add(v);
        else p.getP2().add(v);
        while (!stk.isEmpty()) {
            int u = stk.pop();
            for (int i = 0; i < vertex[u].getList().size(); i++) {
                int next = vertex[u].getList().get(i);
                if (!vertex[next].isMark()) {
                    vertex[next].setMark(true);
                    if (vertex[next].isTag())
                        p.getP1().add(next);
                    else
                        p.getP2().add(next);
                    stk.push(next);
                }
            }
        }
        part.add(p);
    }

    private static void cmp(ArrayList<Part> part,ArrayList<Part> fin){
        int n=part.size();
        for (int i=0;i<(1<<n);i++){
            Part p=new Part();
            for (int j=0;j<n;j++){
                if ((1&(i>>j))==0) {
                    p.getP1().addAll(part.get(j).getP1());
                    p.getP2().addAll(part.get(j).getP2());
                }else{
                    p.getP1().addAll(part.get(j).getP2());
                    p.getP2().addAll(part.get(j).getP1());
                }

            }
            fin.add(p);
        }
    }

}
class Vertex{
    private ArrayList<Integer> list;
    private boolean mark;
    private boolean tag;
    Vertex(){
        list=new ArrayList<>();
        mark=false;
        tag=false;
    }
    ArrayList<Integer> getList(){
        return list;
    }

    boolean isMark() {
        return mark;
    }
    void setTag(boolean t){
        tag=t;
    }
    boolean isTag(){
        return tag;
    }

    void setMark(boolean mark) {
        this.mark = mark;
    }
}
class Part implements Comparable<Part>{
    private ArrayList<Integer> p1;
    private ArrayList<Integer> p2;
    Part(){
        p1=new ArrayList<>();
        p2=new ArrayList<>();
    }

    public ArrayList<Integer> getP1() {
        return p1;
    }

    public ArrayList<Integer> getP2() {
        return p2;
    }

    @Override
    public int compareTo(Part o) {
        if (Math.abs(p1.size()-p2.size())!=Math.abs(o.getP1().size()-o.getP2().size()))
            return Math.abs(p2.size()-p1.size())-Math.abs(o.getP2().size()-o.getP1().size());
        if (p1.size()!=o.getP1().size())
            return p1.size()-o.getP1().size();
        for (int i=0;i<p1.size();i++) {
            if (p1.get(i) != o.getP1().get(i))
                return p1.get(i) - o.getP1().get(i);
        }
        return 0;
    }
}


