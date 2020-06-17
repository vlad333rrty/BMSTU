import java.util.*;
public class Cpm{
    private static Vertex[] vertex=new Vertex[1000];
    private static int cap;
    private static Vertex prev;
    private static Set<Integer> blue=new HashSet<>();
    private static int[] dist;
    private static ArrayList<Pair> passed=new ArrayList<>();
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        scan.useDelimiter("\\Z");
        String[] s=scan.next().split(";");
        ArrayList<String[]> input=new ArrayList<>();
        for (int i=0;i<s.length;i++){
            input.add(s[i].split("<"));
        }
        read(input);
        dist=new int[cap];
        Arrays.fill(dist,Integer.MIN_VALUE);
        for (int i=0;i<cap;i++)
            Find_C(i);
        for (int i=0;i<cap;i++)
            vertex[i].setMark(false);
        for (int i:blue)
            Paint_Blue(i);
        for (int i=0;i<cap;i++)
            vertex[i].setMark(false);
        for (int i=0;i<cap;i++){
            if (!vertex[i].isMark())
                B_F(i);
        }
        Paint_Red();
        StringBuilder fin=new StringBuilder("digraph{\n");
        compose(fin);
        System.out.println(fin);

    }
    private static void read(ArrayList<String[]> input){
        for (String[] i:input){
            int n=i.length;
            boolean flag=true;
            for (int j=0;j<n;j++){
                String cur=i[j].replaceAll("\\s","");
                if (cur.contains("(")){
                    int ind=cur.indexOf("(");
                    String name=cur.substring(0,ind);
                    int weight=Integer.parseInt(cur.substring(ind+1,cur.lastIndexOf(")")));
                    vertex[cap]=new Vertex(cap,weight,name);
                    if (!flag){
                        prev.getList().add(cap);
                    }
                    prev=vertex[cap++];
                    flag=false;
                }else{
                    Vertex v=lookup(cur);
                    if (!flag){
                        prev.getList().add(v.getNumber());
                    }
                    flag=false;
                    prev=v;
                }
            }
        }
    }
    private static Vertex lookup(String name){
        for (int i=0;i<cap;i++){
            if (vertex[i].getName().equals(name))
                return vertex[i];
        }
        return vertex[0];
    }
    private static void Find_C(int v){
        vertex[v].setP(0);
        for (int i=0;i<vertex[v].getList().size();i++){
            int next=vertex[v].getList().get(i);
            if (vertex[next].getPassed()==-1){
                Find_C(next);
            }
            if (vertex[next].getPassed()==0){
                blue.add(next);
            }
        }
        vertex[v].setP(1);
    }
    private static void Paint_Blue(int v){
        Stack<Integer> s=new Stack<>();
        vertex[v].setMark(true);
        vertex[v].setColour("blue");
        s.push(v);
        while (!s.isEmpty()){
            int u=s.pop();
            for (int i=0;i<vertex[u].getList().size();i++){
                int next=vertex[u].getList().get(i);
                if (!vertex[next].isMark()){
                    vertex[next].setMark(true);
                    vertex[next].setColour("blue");
                    s.push(next);
                }
            }
        }
    }
    private static void Paint_Red(){
        ArrayList<Integer> ind=find();
        for (int i:ind){
            Stack<Integer> s=new Stack<>();
            s.add(i);
            while (!s.isEmpty()){
                int v=s.pop();
                if (vertex[v].getColour()==null)
                    vertex[v].setColour("red");
                for (int j:vertex[v].getParents())
                    if (vertex[j].getColour()==null)
                        s.push(j);
            }
        }
    }
    private static ArrayList<Integer> find(){
        int c=0;
        ArrayList<Integer> ret=new ArrayList<>();
        for (int i=0;i<cap;i++){
            if (dist[i]>c){
                c=dist[i];
            }
        }
        for (int i=0;i<cap;i++)
            if (dist[i]==c)
                ret.add(i);
        return ret;
    }
    private static void compose(StringBuilder fin){
        for (int i=0;i<cap;i++){
            fin.append(vertex[i].getName());
            fin.append("[ label = ");
            fin.append("\"");
            fin.append(vertex[i].getName());
            fin.append("(");
            fin.append(vertex[i].getWeight());
            fin.append(")");
            fin.append("\"");
            if (vertex[i].getColour()!=null){
                fin.append(" ,  color = ");
                fin.append(vertex[i].getColour());
            }
            fin.append(" ]\n");
            for (int j=0;j<vertex[i].getList().size();j++){
                Vertex v=vertex[vertex[i].getList().get(j)];
                if (isPainted(i,v.getNumber())) continue;
                fin.append(vertex[i].getName());
                fin.append(" -> ");
                fin.append(v.getName());
                if (v.getParents().contains(i)
                        && v.getColour()!=null
                        && vertex[i].getColour()!=null
                        ||
                        (v.getColour()!=null
                                && vertex[i].getColour()!=null
                                && vertex[i].getColour().equals("blue")
                                && vertex[i].getColour().equals(v.getColour()))){
                    fin.append(" [ color = ");
                    fin.append(vertex[i].getColour());
                    fin.append(" ]");
                    passed.add(new Pair(i,v.getNumber()));
                }
                fin.append("\n");
            }
        }
        fin.append("}\n");
    }
    private static void B_F(int v) {
        dist[v]=vertex[v].getWeight();
        vertex[v].setMark(true);
        while (true){
            boolean flag=false;
            for (int i=0;i<cap;i++){
                for (int j=0;j<vertex[i].getList().size();j++){
                    int next=vertex[i].getList().get(j);
                    if (vertex[next]==vertex[i])
                        vertex[i].setColour("blue");
                    if (vertex[next].getColour()==null && dist[i]>Integer.MIN_VALUE){
                        int len=dist[i]+vertex[next].getWeight();
                        if (dist[next]<len){
                            if (dist[next]>Integer.MIN_VALUE){
                                vertex[next].getParents().clear();
                            }
                            dist[next]=len;
                            vertex[next].getParents().add(i);
                            flag=true;
                        }else{
                            if (dist[next]==len){
                                vertex[next].getParents().add(i);
                            }
                        }
                    }
                    vertex[next].setMark(true);
                }
            }
            if (!flag) break;
        }
    }
    private static boolean isPainted(int from,int to){
        for (Pair p:passed){
            if (p.getFrom()==from && p.getTo()==to)
                return true;
        }
        return false;
    }
}
class Vertex{
    private int weight;
    private String name;
    private int number;
    private ArrayList<Integer> list;
    private int passed;
    private boolean mark;
    private String colour;
    private ArrayList<Integer> parents;
    Vertex(int n,int w,String nm){
        weight=w;
        number=n;
        name=nm;
        passed=-1;
        mark=false;
        colour=null;
        parents=new ArrayList<>();
        list=new ArrayList<>();
    }
    int getWeight(){
        return weight;
    }
    int getNumber(){
        return number;
    }
    ArrayList<Integer> getList(){
        return list;
    }
    String getName(){
        return name;
    }
    void setP(int p){
        passed=p;
    }
    int getPassed(){
        return passed;
    }
    boolean isMark() {
        return mark;
    }
    void setMark(boolean mark) {
        this.mark = mark;
    }
    void setColour(String c){
        colour=c;
    }
    String getColour(){
        return colour;
    }
    ArrayList<Integer> getParents(){
        return parents;
    }
}
class Pair{
    private int from;
    private int to;
    Pair(int f,int t){
        from=f;
        to=t;
    }
    int getFrom(){
        return from;
    }
    int getTo(){
        return to;
    }
}
