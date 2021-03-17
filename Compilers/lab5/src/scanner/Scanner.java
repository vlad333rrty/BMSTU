package scanner;

import data.Fragment;
import data.Position;
import modules.AbstractScanner;
import tokens.*;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Scanner extends AbstractScanner {
    private static final String END_SYMBOL = "$";

    private static final int START = 0;             // 0
    private static final int IDENT = 1;             // 1
    private static final int NUM = 2;               // 2
    private static final int OP = 3;                // 3
    private static final int STR = 4;               // 4
    private static final int ESC = 5;               // 5
    private static final int WHITESPACE = 6;        // 6
    private static final int DEF_1 = 7;             // 7
    private static final int DEF_2 = 8;             // 8
    private static final int DEF_3 = 9;             // 9
    private static final int DEF_4 = 10;            // 10
    private static final int DEF_5 = 11;            // 11
    private static final int TYPEDEF_1 = 12;        // 12
    private static final int TYPEDEF_2 = 13;        // 13
    private static final int TYPEDEF_3 = 14;        // 14
    private static final int TYPEDEF_4 = 15;        // 15
    private static final int TYPEDEF_5 = 16;        // 16
    private static final int TYPEDEF_6 = 17;        // 17
    private static final int K_I = 18;              // 18

    private static final int DROP_RETURN = 28;
    private static final int RETURN = 29;
    private static final int KEYWORD = 30;
    private static final int ERROR = -1;
    private static final int END = -2;

    private static final int DIGIT_CODE = 0;
    private static final int D_CODE = 1;
    private static final int E_CODE = 2;
    private static final int F_CODE = 3;
    private static final int I_CODE = 4;
    private static final int N_CODE = 5;
    private static final int P_CODE = 6;
    private static final int T_CODE = 7;
    private static final int Y_CODE = 8;
    private static final int REST_LETTER_CODE = 9;
    private static final int OP_CODE = 10;
    private static final int STR_TAG_CODE = 11;
    private static final int BACKSLASH_CODE = 12;
    private static final int WHITESPACE_CODE =13;
    private static final int OTHER_CODE = 14;
    private static final int END_CODE = 15;

    /**
     * [0-9]        0
     * d            1
     * e            2
     * f            3
     * i            4
     * n            5
     * p            6
     * t            7
     * y            8
     * [a-z]        9
     * [#;]         10
     * %            11
     * \            12
     * \\s          13
     * .            14
     * $            15 eof
     */
    private final int[][] table = {
          // 0      1      2      3      4     5      6         7        8      9     10       11        12      13          14
            {NUM,  DEF_1, IDENT, IDENT, IDENT, IDENT, IDENT, TYPEDEF_1, IDENT, IDENT, OP,      STR,     ERROR,  WHITESPACE,  ERROR, END }, //START
            {IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT,   IDENT, IDENT, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN,DROP_RETURN}, //IDENT
            {NUM, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN,DROP_RETURN},//NUM
            {DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN, DROP_RETURN,DROP_RETURN},//OP
            {STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,RETURN,ESC,STR,STR,STR},//str
            {STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR,STR},//escape
            {START,START,START,START,START,START,START,START,START,START,START,START,START,WHITESPACE,START,START},//whitespace
            {IDENT,IDENT,DEF_2,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//DEF1
            {IDENT,IDENT,IDENT,DEF_3,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//DEF2
            {IDENT,IDENT,IDENT,IDENT,DEF_4,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//DEF3
            {IDENT,IDENT,IDENT,IDENT,IDENT,DEF_5,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//DEF4
            {IDENT,IDENT, K_I,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//DEF5
            {IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,TYPEDEF_2,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF1
            {IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,TYPEDEF_3,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF2
            {IDENT,IDENT,TYPEDEF_4,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF3
            {IDENT,TYPEDEF_5,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF4
            {IDENT,IDENT,TYPEDEF_6,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF5
            {IDENT,IDENT,IDENT,K_I,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN,DROP_RETURN},//TYPEDEF6
            {IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, KEYWORD, KEYWORD, KEYWORD, KEYWORD, KEYWORD,KEYWORD} // K_I
    };

    private final Position position = new Position(1,1,0);
    private StringBuilder value = new StringBuilder();
    private final Logger logger = Logger.getLogger(Scanner.class.getName());

    public Scanner(String program) {
        super(program.concat(END_SYMBOL));
        logger.setUseParentHandlers(false);
        try{
            FileHandler fh = new FileHandler("log.log");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fh.setFormatter(simpleFormatter);
            logger.addHandler(fh);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IToken getNextToken() {
        int currentState = START;
        value = new StringBuilder();
        Position startPos = new Position(position);
        while (true){
            char symbol = peekNextSymbol();
            int num = getLabelNumber(symbol);
            int nextState = table[currentState][num];
            switch (nextState){
                case END:
                    return EndToken.getInstance();
                case ERROR:
                    takeNextSymbol();
                    return new ErrorToken(value.toString(),new Fragment(startPos,position));
                case DROP_RETURN:
                    return onReturn(startPos, currentState);
                case RETURN:
                    takeNextSymbol();
                    return onReturn(startPos,currentState);
                case KEYWORD:
                    return onReturn(startPos, nextState);
                case START:
                    return getNextToken();
                default:
                    takeNextSymbol();
                    logger.info(String.format("%d -> %d, %s", currentState,nextState,value.toString()));
                    currentState = nextState;
            }
        }
    }

    private IToken onReturn(Position start,int state){
        Fragment fragment = new Fragment(start,position);
        switch (state){
            case IDENT:
            case DEF_1:
            case DEF_2:
            case DEF_3:
            case DEF_4:
            case DEF_5:
            case TYPEDEF_1:
            case TYPEDEF_2:
            case TYPEDEF_3:
            case TYPEDEF_4:
            case TYPEDEF_5:
            case TYPEDEF_6:
                return new IdentToken(value.toString(),fragment);
            case NUM:
                return new NumberToken(value.toString(),fragment);
            case OP:
                return new OperationToken(value.toString(),fragment);
            case KEYWORD:
                return new KeyWordToken(value.toString(),fragment);
            case STR:
                return new StringToken(interpretString(value.toString()),fragment);
            default:
                throw new IllegalArgumentException("Unexpected state: " + state);
        }
    }

    private String interpretString(String str){
        return str.substring(1,str.length()-1).replaceAll("\\\\","");
    }

    private void incLineIfNeeded(char c){
        if (c == '\n'){
            position.incLine();
            position.setPos(1);
        }
    }

    private void takeNextSymbol(){
        char symbol = program.charAt(position.goToNextSymbol());
        value.append(symbol);
        incLineIfNeeded(symbol);
    }

    private char peekNextSymbol(){
        return program.charAt(position.getIndex());
    }

    private int getLabelNumber(char symbol){
        if (Character.isDigit(symbol)){
            return DIGIT_CODE;
        }
        if (symbol == 'd'){
            return D_CODE;
        }
        if (symbol == 'e'){
            return E_CODE;
        }
        if (symbol == 'f'){
            return F_CODE;
        }
        if (symbol == 'i'){
            return I_CODE;
        }
        if (symbol == 'n'){
            return N_CODE;
        }
        if (symbol == 'p'){
            return P_CODE;
        }
        if (symbol == 't'){
            return T_CODE;
        }
        if (symbol == 'y'){
            return Y_CODE;
        }
        if (Character.isLetter(symbol)){
            return REST_LETTER_CODE;
        }
        if (symbol == '#' || symbol == ';'){
            return OP_CODE;
        }
        if (symbol == '%'){
            return STR_TAG_CODE;
        }
        if (symbol == '\\'){
            return BACKSLASH_CODE;
        }
        if (Character.isWhitespace(symbol)){
            return WHITESPACE_CODE;
        }
        if (symbol == '$'){
            return END_CODE;
        }
        return OTHER_CODE;
    }
}