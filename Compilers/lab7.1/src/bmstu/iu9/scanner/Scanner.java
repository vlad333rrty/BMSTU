package bmstu.iu9.scanner;

import bmstu.iu9.data.Fragment;
import bmstu.iu9.data.Position;
import bmstu.iu9.tokens.*;

public class Scanner extends AbstractScanner {

    // States
    private static final int START = 0;
    private static final int NAME = 1;
    private static final int NAME_Q = 2;
    private static final int D_Q = 3;
    private static final int LEFT_BRACE = 4;
    private static final int RIGHT_BRACE = 5;
    private static final int LEFT_SQ_BR = 6;
    private static final int RIGHT_SQ_BR = 7;
    private static final int COMMA = 8;
    private static final int AT = 9;
    private static final int COLON = 10;
    private static final int WS = 11;

    // Action statuses
    private static final int END = -3;
    private static final int ERROR = -1;
    private static final int RETURN = -2; // take next symbol and return
    private static final int S_R = -4; // [STAY_RETURN] don't take next symbol, just return collected value

    // Codes
    private static final int LEFT_BRACE_CODE = 0;
    private static final int LETTER_CODE = 1;
    private static final int RIGHT_BRACE_CODE = 2;
    private static final int QUOTE_CODE = 3;
    private static final int COMMA_CODE = 4;
    private static final int LEFT_SQ_BR_CODE = 5;
    private static final int RIGHT_SQ_BR_CODE = 6;
    private static final int COLON_CODE = 7;
    private static final int AT_CODE = 8;
    private static final int DOUBLE_QUOTE_CODE = 9;
    private static final int END_CODE = 10;
    private static final int OTHER_CODE = 11;
    private static final int WS_CODE = 12;

    private static final int[][] TABLE = {
            {LEFT_BRACE,NAME,RIGHT_BRACE,ERROR,COMMA,LEFT_SQ_BR,RIGHT_SQ_BR,COLON,AT, D_Q,END,ERROR,WS}, //START
            {ERROR,NAME, S_R,NAME_Q, S_R, S_R, S_R, S_R, S_R, S_R,ERROR,ERROR,S_R}, //NAME
            {ERROR, S_R, S_R,ERROR, S_R,ERROR, S_R, S_R, S_R, S_R,ERROR,ERROR,S_R}, //NAME_Q
            {D_Q, D_Q, D_Q, D_Q, D_Q, D_Q, D_Q, D_Q, D_Q,RETURN, D_Q, D_Q,D_Q},// DOUBLE_QUOTE
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // LEFT_BRACE
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // RIGHT_BRACE
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // LEFT_SQ_BR
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // RIGHT_SQ_BR
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // COMMA
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, //AT
            {S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R, S_R,S_R}, // COLON
            {START,START,START,START,START,START,START,START,START,START,START,START,WS}, //WHITESPACE [WS]
    };

    private final Position position = new Position(1, 1, 0);
    private StringBuilder value = new StringBuilder();

    public Scanner(String program) {
        super(program.concat(EndToken.getValue()));
    }

    @Override
    public IToken getNextToken() {
        int currentState = START;
        value = new StringBuilder();
        Position startPos = new Position(position);
        while (true) {
            char symbol = peekNextSymbol();
            int label = getLabelNumber(symbol);
            int nextState = TABLE[currentState][label];
            switch (nextState) {
                case END:
                    return EndToken.getInstance();
                case ERROR:
                    takeNextSymbol();
                    return new ErrorToken(value.toString(), new Fragment(startPos, position));
                case RETURN:
                    takeNextSymbol();
                    return onReturn(startPos, currentState,value.toString());
                case S_R:
                    return onReturn(startPos, currentState, value.toString());
                case START:
                    return getNextToken();
                default:
                    takeNextSymbol();
                    currentState = nextState;
            }
        }
    }

    private IToken onReturn(Position start, int state, String value) {
        Fragment fragment = new Fragment(start, new Position(position));
        switch (state) {
            case NAME:
            case NAME_Q:
                return new NonTerminalToken(value,fragment);
            case D_Q:
                return new ValToken(value,fragment);
            case LEFT_BRACE:
                return new LeftBraceToken(value,fragment);
            case RIGHT_BRACE:
                return new RightBraceToken(value,fragment);
            case LEFT_SQ_BR:
                return new LeftSquareBracketToken(value,fragment);
            case RIGHT_SQ_BR:
                return new RightSquareBracketToken(value,fragment);
            case AT:
                return new AtToken(value,fragment);
            case COLON:
                return new ColonToken(value,fragment);
            case COMMA:
                return new CommaToken(value,fragment);
            default:
                throw new IllegalArgumentException("Unexpected state: " + state);
        }
    }


    private void incLineIfNeeded(char c) {
        if (c == '\n') {
            position.incLine();
            position.setPos(1);
        }
    }

    private void takeNextSymbol() {
        char symbol = program.charAt(position.goToNextSymbol());
        value.append(symbol);
        incLineIfNeeded(symbol);
    }

    private char peekNextSymbol() {
        return program.charAt(position.getIndex());
    }

    private int getLabelNumber(char symbol) {
        switch (symbol){
            case '{':
                return LEFT_BRACE_CODE;
            case '}':
                return RIGHT_BRACE_CODE;
            case '\'':
                return QUOTE_CODE;
            case ',':
                return COMMA_CODE;
            case '[':
                return LEFT_SQ_BR_CODE;
            case ']':
                return RIGHT_SQ_BR_CODE;
            case ':':
                return COLON_CODE;
            case '@':
                return AT_CODE;
            case '\"':
                return DOUBLE_QUOTE_CODE;
            case '$':
                return END_CODE;
            case '\n':
            case ' ':
            case '\t':
                return WS_CODE;
            default:
                if (Character.isLetter(symbol)){
                    return LETTER_CODE;
                }else {
                    return OTHER_CODE;
                }
        }
    }
}
