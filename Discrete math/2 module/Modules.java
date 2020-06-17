import java.util.*;
public class Modules {
    private static Vertex[] vertex = new Vertex[500];
    private static int ind;
    private static int time = 1;
    private static int count = 1;
    private static ArrayList<Function> funcs = new ArrayList<>();
    private static ArrayList<String> list;
    private static int curlex;
    private static Function currentFunc;
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\Z");
        String input = scan.next();
        list = lexer(input);
        if (!parse()) {
            System.out.println("error");
            return;
        }
        Tarjan();
        System.out.println(count - 1);
    }
    private static ArrayList<String> lexer(String input) {
        ArrayList<String> list = new ArrayList<>();
        int n = input.length();
        boolean flag = true;
        String cur = "";
        for (int i = 0; i < n; ) {
            if (Character.isWhitespace(input.charAt(i))) {
                i++;
                continue;
            }
            if (Character.isDigit(input.charAt(i))) {
                StringBuilder temp = new StringBuilder();
                while (i < n && Character.isDigit(input.charAt(i)))
                    temp.append(input.charAt(i++));
                list.add(temp.toString());
                continue;
            }
            if (Character.isLetter(input.charAt(i))) {
                StringBuilder temp = new StringBuilder();
                while (i < n && (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i))))
                    temp.append(input.charAt(i++));
                if (i < n && input.charAt(i) == '(') {
                    int k=i;
                    String name=temp.toString();
                    if (lookup(temp.toString()) == null) {
                        vertex[ind] = new Vertex(name, ind++);
                    }
                    char delimiter;
                    if (flag) {
                        cur = temp.toString();
                        delimiter = ':';
                    } else {
                        vertex[lookup(cur).getNumber()].getList().add(lookup(name).getNumber());
                        delimiter = ';';
                    }
                    int a=0;
                    ArrayList<String> args=new ArrayList<>();
                    while (i < n && input.charAt(i) != delimiter && input.charAt(i) != ':') {
                        char c=input.charAt(i);
                        if (c==',')
                            a++;
                        if (Character.isLetter(c))
                            args.add(c+"");
                        i++;
                    }
                    if (args.isEmpty())
                        a=-1;
                    if (flag){
                        Function f=new Function(name,a+1);
                        f.setArgList(args);
                        funcs.add(f);
                    }
                    flag=false;
                    i=k;
                }
                list.add(temp.toString());
            } else {
                if (input.charAt(i) == ':' && input.charAt(i + 1) == '=') {
                    list.add(input.charAt(i) + "" + input.charAt(++i));
                    i++;
                    continue;
                }
                if (input.charAt(i) == ';') {
                    flag = true;
                    list.add(input.charAt(i++) + "");
                } else {
                    if (comps(input.charAt(i)) && comps(input.charAt(i + 1))) {
                        list.add(input.charAt(i) + "" + input.charAt(++i));
                        i++;
                        continue;
                    }
                    list.add(input.charAt(i++) + "");
                }
            }
        }
        return list;
    }
    private static Vertex lookup(String name) {
        for (int i = 0; i < ind; i++)
            if (vertex[i].getName().equals(name))
                return vertex[i];
        return null;
    }

    private static void Tarjan() {
        Stack<Vertex> s = new Stack<>();
        for (int i = 0; i < ind; i++) {
            if (vertex[i].getT1() == 0)
                visitVertex(vertex[i], s);
        }
    }

    private static void visitVertex(Vertex v, Stack<Vertex> stack) {
        v.setT1(time);
        v.setLow(time++);
        stack.push(v);
        for (int i = 0; i < v.getList().size(); i++) {
            Vertex u = vertex[v.getList().get(i)];
            if (u.getT1() == 0)
                visitVertex(u, stack);
            if (u.getComp() == 0 && v.getLow() > u.getLow())
                v.setLow(u.getLow());
        }
        if (v.getT1() == v.getLow()) {
            Vertex u;
            do {
                u = stack.pop();
                u.setComp(count);
            } while (u != v);
            count++;
        }
    }

    private static boolean comps(char c) {
        return c == '<' || c == '>' || c == '=';
    }

    private static boolean comparison(String s) {
        return s.equals(">") || s.equals("<") || s.equals(">=") || s.equals("<=") || s.equals("<>") || s.equals("=");
    }
    private static Function has(String name) {
        for (Function f : funcs) {
            if (f.getName().equals(name))
                return f;
        }
        return null;
    }
    private static boolean parse(){
        while (curlex<list.size()){
            String name=list.get(curlex++);
            if ((currentFunc=has(name))==null)
                return false;
            if (!parseAr())
                return false;
            if (!parseExpr())
                return false;
            if (!list.get(curlex++).equals(";"))
                return false;
        }
        return true;
    }
    private static boolean parseAr(){
        return list.get(curlex++).equals("(")
                && parsePrn(currentFunc)
                && list.get(curlex++).equals(")")
                && list.get(curlex++).equals(":=");
    }
    private static boolean parsePrn(Function f){
        if (list.get(curlex).equals(")"))
            return true;
        boolean a=parseExpr();
        int args=0;
        while (a && list.get(curlex).equals(",")){
            curlex++;
            args++;
            if (args+1>f.getArgs())
                return false;
            a=parseExpr();
        }
        return a;
    }
    private static boolean parseExpr(){
        boolean a=parseC();
        if (!a)
            return false;
        if (list.get(curlex).equals("?")){
            curlex++;
            return parseC() && list.get(curlex++).equals(":") && parseExpr();
        }
        return true;
    }
    private static boolean parseC(){
        boolean a=parseArth();
        if (a && comparison(list.get(curlex))){
            curlex++;
            a=parseArth();
        }
        return a;
    }
    private static boolean parseArth(){
        boolean a=parseT();
        while (a && (list.get(curlex).equals("+") || list.get(curlex).equals("-"))){
            curlex++;
            a=parseT();
        }
        return a;
    }
    private static boolean parseT(){
        boolean a=parseF();
        while (a && curlex<list.size() && (list.get(curlex).equals("*") || list.get(curlex).equals("/"))){
            curlex++;
            a=parseF();
        }
        return a && curlex<list.size();
    }
    private static boolean parseF(){
        boolean res;
        Function f;
        if (curlex>=list.size())
            return false;
        if (list.get(curlex).equals("-")){
            curlex++;
            res=parseF();
        }else {
            if (Character.isDigit(list.get(curlex).charAt(0))
                    || currentFunc.getArgList().contains(list.get(curlex))) {
                curlex++;
                return true;
            }
            if ((f = has(list.get(curlex))) != null) {
                curlex++;
                return list.get(curlex++).equals("(")
                        && parsePrn(f)
                        && list.get(curlex++).equals(")");
            }
            if (list.get(curlex).equals("(")) {
                curlex++;
                res = parseExpr();
                if (curlex < list.size() && list.get(curlex).equals(")"))
                    curlex++;
                else
                    return false;
            } else
                return false;

        }
        return res;
    }
}
class Vertex{
    private String name;
    private ArrayList<Integer> list;
    private int number;
    private int low,t1,comp;
    Vertex(String n,int num){
        name=n;
        number=num;
        low=t1=comp=0;
        list=new ArrayList<>();
    }
    ArrayList<Integer> getList(){
        return list;
    }
    String getName(){
        return name;
    }
    int getNumber(){
        return number;
    }
    int getLow(){
        return low;
    }
    int getT1(){
        return t1;
    }
    void setLow(int l){
        low=l;
    }
    void setT1(int t){
        t1=t;
    }

    void setComp(int comp) {
        this.comp = comp;
    }

    int getComp() {
        return comp;
    }
}
class Function{
    private int args;
    private String name;
    private ArrayList<String> argList;
    Function(String x,int y){
        name=x;
        args=y;
        argList=new ArrayList<>();
    }

    int getArgs() {
        return args;
    }
    String getName(){
        return name;
    }
    ArrayList<String> getArgList(){
        return argList;
    }

    void setArgList(ArrayList<String> argList) {
        this.argList = argList;
    }
}
