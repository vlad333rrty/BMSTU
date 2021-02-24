import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static final String ERROR_MESSAGE="syntax error";

    private final Queue<Token> tokenQueue=new ArrayDeque<>();

    private final String input;
    private final Matcher matcher;
    private int lastMatchEnd,lastMatchStart,line=1,pos=1;

    public Lexer(String input) {
        this.input = input.replaceAll("\n*$","");
        String string = "\"((\"\")|(\\\\\n)|([^\"\n\\\\]))*\"";
        String number ="([0-9]+)|(\\$[0-9A-Fa-f]+)";
        String ident = "\\p{L}[\\p{L}0-9$]+";
        Pattern pattern = Pattern.compile("(?<ident>" + ident + ")|(?<number>" + number + ")|(?<string>" + string + ")");
        matcher = pattern.matcher(input);
    }

    public Token getNextToken() {
        if (!tokenQueue.isEmpty()){
            return tokenQueue.poll();
        }
        if (matcher.find()) {
            if (isSyntaxError(lastMatchEnd,matcher.start())) {
                int oldPos=pos;
                updateState();
                tokenQueue.add(getToken());
                return new Token(ERROR_MESSAGE, Token.TokenType.ERROR, line, oldPos);
            }
            updateState();
            return getToken();
        }
        updatePos(lastMatchEnd,input.length());
        if (lastMatchEnd < input.length()) {
            lastMatchEnd = input.length();
            return new Token(ERROR_MESSAGE, Token.TokenType.ERROR, line, pos);
        }
        return Token.getEndToken();
    }

    private void updatePos(int startPos, int endPos) {
        for (int i = startPos; i < endPos; i++) {
            if (input.charAt(i) == '\n') {
                line++;
                pos = 1;
            } else {
                pos++;
            }
        }
    }

    private boolean isSyntaxError(int startPos,int endPos) {
        return lastMatchEnd < matcher.start() && !input.substring(startPos, endPos).chars().allMatch(Character::isWhitespace);
    }

    private Token getToken(){
        if (matcher.group("ident") != null) {
            return new Token(matcher.group("ident"), Token.TokenType.IDENT, line, pos);
        }
        if (matcher.group("number") != null) {
            return new Token(matcher.group("number"), Token.TokenType.NUMBER, line, pos);
        }
        if (matcher.group("string") != null) {
            return new Token(matcher.group("string"), Token.TokenType.STRING, line, pos);
        }
        throw new IllegalStateException("Unknown token type");
    }

    private void updateState(){
        updatePos(lastMatchStart, matcher.start());
        lastMatchStart = matcher.start();
        lastMatchEnd = matcher.end();
    }
}
