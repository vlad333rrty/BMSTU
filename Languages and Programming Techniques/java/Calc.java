import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

public class Calc {
    private static HashMap<String,Integer> table=new HashMap<>();
    private static ArrayList<String> lexemes=new ArrayList<>();
    private static int ind=0;
    private static Scanner scanner=new Scanner(System.in);
    public static void main(String[] args) throws ParseError {
        String s=scanner.nextLine();
        lexer(s);
        int res=parse();
        if (ind<lexemes.size()) throw new ParseError();
        System.out.println(res);
    }

    private static void lexer(String seq){
        for (int i=0;i<seq.length();){
            char c=seq.charAt(i);
            if (Character.isDigit(c)){
                i= addLexeme(i,seq,Character::isDigit);
            }else if (Character.isLetter(c)){
                i= addLexeme(i,seq,(ch)->Character.isLetter(ch) || Character.isDigit(ch));
            }else if (Character.isWhitespace(c)){
                i++;
            }else{
                lexemes.add(c + "");
                i++;
            }
        }
    }

    private static int addLexeme(int i, String seq, Function<Character,Boolean> pred){
        StringBuilder builder=new StringBuilder();
        for (char c; i<seq.length(); i++){
            c=seq.charAt(i);
            if (pred.apply(c)) builder.append(c);
            else break;
        }
        String res=builder.toString();
        lexemes.add(res);
        if (Character.isLetter(builder.charAt(0)) && !table.containsKey(res)){
            table.put(res,scanner.nextInt());
        }
        return i;
    }

    private static boolean isDigit(String s){
        return Character.isDigit(s.charAt(0));
    }

    private static int parse() throws ParseError {
        int expr=parseA();
        while (ind<lexemes.size() && (lexemes.get(ind).equals("+") || lexemes.get(ind).equals("-"))){
            if (lexemes.get(ind).equals("+")) {
                ind++;
                expr += parseA();
            }
            else {
                ind++;
                expr -= parseA();
            }
        }
        return expr;
    }

    private static int parseA() throws ParseError {
        int expr=parseB();
        while (ind<lexemes.size() && (lexemes.get(ind).equals("*") || lexemes.get(ind).equals("/"))){
            if (lexemes.get(ind++).equals("*")) expr*=parseB();
            else expr/=parseB();
        }
        return expr;
    }

    private static int parseB() throws ParseError {
        if (ind==lexemes.size()) throw new ParseError();
        int expr;
        if (isDigit(lexemes.get(ind))){
            expr=Integer.parseInt(lexemes.get(ind++));
        }else if (lexemes.get(ind).equals("(")){
            ind++;
            expr=parse();
            if (ind<lexemes.size() && lexemes.get(ind).equals(")")) ind++;
            else throw new ParseError();
        }else if (lexemes.get(ind).equals("-")){
            ind++;
            expr=-parseB();
        }else if (Character.isLetter(lexemes.get(ind).charAt(0))){
            expr=table.get(lexemes.get(ind++));
        }else throw new ParseError();
        return expr;
    }
}

class ParseError extends Exception{
    ParseError(){
        System.out.println("error");
        System.exit(0);
    }
}

