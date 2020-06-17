import java.util.*;
public class GraphBase {
    private static int[][] matrix;
    private static Vertex[] vertex;
    private static int[][] reversed;
    private static boolean[] v;
    private static int[][] sc;
    public static void main(String[] args) {
        Scanner scan=new Scanner(System.in);
        int n=scan.nextInt();
        int k=scan.nextInt();
        matrix=new int[n][n];
        vertex=new Vertex[n];
        reversed=new int[n][n];
        v=new boolean[n];
        for (int i=0;i<k;i++){
            int a=scan.nextInt();
            int b=scan.nextInt();
            if (!v[a]){
                vertex[a]=new Vertex();
                v[a]=true;
            }
            if (!v[b]){
                vertex[b]=new Vertex();
                v[b]=true;
            }
            matrix[a][b]=1;
            reversed[b][a]=1; 
        }
        ArrayList<Integer> list=new ArrayList<>();
        ArrayList<Comp> comp=new ArrayList<>();
        for (int i=0;i<n;i++){
            if (v[i] && !vertex[i].isMark())
                dfs1(list,i);
        }
        setFalse();
        int temp=list.size(),p=0;
        for (int i=0;i<temp;i++){
            if (v[i] && !vertex[list.get(temp-1-i)].isMark()){
                Comp c=new Comp(p++);
                dfs2(c,list.get(temp-1-i));
                comp.add(c);
            }
        }
        int l=comp.size();
        sc=new int[l][l];
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                if (matrix[i][j]==1){
                    int t1=vertex[i].getCompNumber();
                    int t2=vertex[j].getCompNumber();
                    if (t1!=t2 && t1>=0 && t2>=0){
                        sc[t1][t2]=1;
                    }
                }
            }
        }
        System.out.println();
        search(comp);
    }
    private static void search(ArrayList<Comp> comp){
        int l=sc[0].length;
        ArrayList<Comp> next=new ArrayList<>();
        for (int i=0,j;i<l;i++){
            for (j=0;j<l;j++){
                if (sc[j][i]!=0)
                    break;
            }
            if (j==l)
                next.add(comp.get(i));
        }
        ArrayList<Integer> fin=new ArrayList<>();
        for (Comp c:next) {
            Collections.sort(c.getList());
            fin.add(c.getList().get(0));
        }
        fillFin(fin);
        Collections.sort(fin);
        for (int i:fin)
            System.out.print(i+" ");
        System.out.println();
    }
    
    private static void fillFin(ArrayList<Integer> fin){
        for (int i=0;i<v.length;i++){
            if (!v[i])
                fin.add(i);
        }
    }
    
    private static void print(int[][] m,int n){
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++)
                System.out.print(m[i][j]+" ");
            System.out.println();
        }
        System.out.println();
    }
    private static void setFalse(){
        for (int i=0;i<vertex.length;i++)
            if (v[i])
                vertex[i].setMark(false);
    }
    private static void dfs1(ArrayList<Integer> list,int v){
        vertex[v].setMark(true);
        for (int i=0;i<matrix[v].length;i++){
            if (matrix[v][i]==1 && !vertex[i].isMark()){
                dfs1(list,i);
            }
        }
        list.add(v);
    }
    private static void dfs2(Comp comp,int v){
        vertex[v].setMark(true);
        vertex[v].setCompNumber(comp.getN());
        comp.getList().add(v);
        for (int i=0;i<reversed[v].length;i++){
            if (reversed[v][i]==1 && !vertex[i].isMark()){
                dfs2(comp,i);
            }
        }
    }
}
class Vertex{
    private int compNumber=-1;
    private boolean mark;
    void setMark(boolean mark){
        this.mark=mark;
    }
    boolean isMark(){
        return mark;
    }
    void setCompNumber(int n){
        compNumber=n;
    }
    int getCompNumber(){
        return compNumber;
    }
}
class Comp{
    private ArrayList<Integer> list;
    private int n;
    Comp(int n){
        list=new ArrayList<>();
        this.n=n;
    }
    int getN(){
        return n;
    }
    ArrayList<Integer> getList(){
        return list;
    }
}

