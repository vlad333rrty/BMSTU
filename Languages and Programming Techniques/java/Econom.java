import java.util.HashSet;
import java.util.Scanner;

public class Econom{
    private static final HashSet<String> set=new HashSet<>();
    private static int ind;
    private static String input;
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        input=scanner.nextLine();
        parse();
        System.out.println(set.size());
    }

    private static String parse(){
        StringBuilder expr=new StringBuilder();
        if (input.charAt(ind)=='(') expr.append(input.charAt(ind++));
        if (isOp(input.charAt(ind))){
            expr.append(input.charAt(ind++));
            expr.append(parse());
            expr.append(parse());
            set.add(expr.toString());
        }
        expr.append(input.charAt(ind++));
        return expr.toString();
    }

    private static boolean isOp(char c){
        return c=='@' || c=='#' || c=='$';
    }
}
