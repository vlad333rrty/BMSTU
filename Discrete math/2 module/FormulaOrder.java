import java.util.*;
public class FormulaOrder{
    private static ArrayList<String> list=new ArrayList<>();
    private static Vertex[] vertex=new Vertex[1000000];
    private static Vertex currentV;
    private static int cap,ind;
    private static Vertex[] vm=new Vertex[1000000];
    private static Map<String,Integer> map=new HashMap<>();
    private static boolean flag=false;
    private static ArrayList<Integer> order=new ArrayList<>();
    private static ArrayList<String> lists=new ArrayList<>();
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        scan.useDelimiter("\\Z");
        String input=scan.next().replaceAll("\n",";")+";";
        if (lexer(input) && parse()){
            for (int i=0;i<cap;i++){
                if (vertex[i].getMark()==-1)
                    Tarjan(i);
                if (flag){
                    System.out.println("cycle");
                    return;
                }
            }
            print();
        }else
            System.out.println("syntax error");

    }
    private static void print(){
        for (int i:order){
            System.out.println(lists.get(i));
        }
    }
    private static boolean lexer(String input){
        int n=input.length();
        boolean flag=true;
        int l=0;
        StringBuilder eq=new StringBuilder();
        for (int i=0;i<n;){
            if (Character.isWhitespace(input.charAt(i))){
                i++;
                continue;
            }
            if (input.charAt(i)==';'){
                flag=true;
                i++;
                list.add(";");
                lists.add(eq.toString());
                eq=new StringBuilder();
                l++;
                continue;
            }
            if (Character.isLetter(input.charAt(i))){
                StringBuilder temp=new StringBuilder();
                if (flag){
                    vertex[cap]=new Vertex(cap);
                    int k=i;
                    StringBuilder name=new StringBuilder();
                    while (i<n && input.charAt(i)!='='){
                        if (Character.isWhitespace(input.charAt(i))){
                            i++;
                            continue;
                        }
                        if (input.charAt(i)==',') {
                            vertex[cap].getName().add(name.toString());
                            map.put(name.toString(),cap);
                            name=new StringBuilder();
                        }else
                            name.append(input.charAt(i));
                        i++;
                    }
                    String s=name.toString();
                    if (vertex[cap].getName().contains(s) || map.containsKey(s)){
                        return false;
                    }
                    vertex[cap].getName().add(s);
                    map.put(s,cap);
                    vm[l]=vertex[cap];
                    i=k;
                    cap++;
                    flag=false;
                }else{
                    while (i<n && (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i)))){
                        temp.append(input.charAt(i));
                        eq.append(input.charAt(i++));
                    }
                    String name=temp.toString();
                    list.add(name);
                    l++;
                }
            }else{
                if (Character.isDigit(input.charAt(i))){
                    StringBuilder temp=new StringBuilder();
                    while (i<n && Character.isDigit(input.charAt(i))) {
                        temp.append(input.charAt(i));
                        eq.append(input.charAt(i++));
                    }
                    list.add(temp.toString());
                    l++;
                }else{
                    if (input.charAt(i)=='='){
                        list.add("=");
                        eq.append("=");
                        i++;
                        l++;
                        flag=false;
                    }else {
                        list.add(input.charAt(i) + "");
                        eq.append(input.charAt(i++));
                        l++;
                    }
                }
            }
        }
        return true;
    }
    private static void Tarjan(int v){
        Stack<Integer> s=new Stack<>();
        vertex[v].setMark(0);
        s.push(v);
        while (!s.isEmpty()){
            int u=s.pop();
            for (int i:vertex[u].getList()){
                if (vertex[i].getMark()==-1)
                    Tarjan(i);
                if (vertex[i].getMark()==0){
                    flag=true;
                    return;
                }
            }
            vertex[u].setMark(1);
            order.add(u);
        }
    }
    private static boolean parse(){
        while (ind<list.size()){
            currentV=vm[ind];
            if (!parseAr())
                return false;
            if (!parseR())
                return false;
            if (!list.get(ind++).equals(";"))
                return false;
        }
        return true;
    }
    private static boolean parseAr(){
        boolean a=parseG();
        while (a && list.get(ind).equals(",")){
            ind++;
            a=parseG();
        }
        return a && list.get(ind++).equals("=");
    }
    private static boolean parseG(){
        return ind<list.size() && Character.isLetter(list.get(ind++).charAt(0));
    }
    private static boolean parseR(){
        boolean a=parseExpr();
        int args=0;
        while (a && ind<list.size() && list.get(ind).equals(",")){
            ind++;
            args++;
            a=parseExpr();
        }
        return a && args+1==currentV.getName().size() && ind<list.size();
    }
    private static boolean parseExpr(){
        boolean a=parseT();
        while (a && ind<list.size() && (list.get(ind).equals("+") || list.get(ind).equals("-"))){
            ind++;
            a=parseT();
        }
        return a;
    }
    private static boolean parseT(){
        boolean a=parseF();
        while (a && ind<list.size() && (list.get(ind).equals("*") || list.get(ind).equals("/"))){
            ind++;
            a=parseF();
        }
        return a;
    }
    private static boolean parseF(){
        if (ind>=list.size())
            return false;
        boolean res;
        if (list.get(ind).equals("-")){
            ind++;
            res=parseF();
        }else{
            if (Character.isLetter(list.get(ind).charAt(0))){
                try {
                    Vertex v = vertex[map.get(list.get(ind))];
                    currentV.getList().add(v.getNumber());
                    ind++;
                    return true;
                }catch (NullPointerException e){
                    return false;
                }
            }
            if (Character.isDigit(list.get(ind).charAt(0))){
                ind++;
                return true;
            }
            if (list.get(ind).equals("(")){
                ind++;
                res=parseExpr();
                if (ind<list.size() && list.get(ind).equals(")"))
                    ind++;
                else return false;
            }else return false;
        }
        return res;
    }
}
class Vertex{
    private HashSet<Integer> list;
    private int mark;
    private int number;
    private ArrayList<String> name;
    Vertex(int n){
        number=n;
        mark=-1;
        list=new HashSet<>();
        name=new ArrayList<>();
    }
    HashSet<Integer> getList(){
        return list;
    }
    int getMark(){
        return mark;
    }
    int getNumber(){
        return number;
    }
    ArrayList<String> getName(){
        return name;
    }
    void setMark(int i){
        mark=i;
    }
}
