package scanner;

import data.*;
import modules.AbstractScanner;
import tokens.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static format.FormatUtils.format;

public class Scanner extends AbstractScanner {
    private static final char CONST_INT_START='#';
    private static final char IDENT_START_1='.';
    private static final char IDENT_START_2=':';
    private static final char ARRAY_START_1=',';
    private static final char ARRAY_START_2=';';
    private static final char END_SYMBOL='!';
    private static final char NEW_LINE='\n';
    private static final char SPACE = ' ';
    private static final char TAB = '\t';
    private static final char PLEASE_START = 'P';
    private static final char DO_START = 'D';
    private static final char FORGET_START = 'F';

    private static final String DECIMAL = "Decimal";
    private static final String KEY_WORD_PLEASE = "PLEASE";
    private static final String KEY_WORD_DO = "DO";
    private static final String KEY_WORD_FORGET = "FORGET";

    private final IMessageList messageList = new MessageList();

    private final Position pos = new Position(1,0,0);
    private static final Logger logger=Logger.getLogger(Scanner.class.getName());

    public Scanner(String program) {
        super(program.replaceAll("\n+$", "").concat(String.format("%s",END_SYMBOL)));
    }

    @Override
    public IToken getNextToken() {
        if (finished()){
            return EndToken.getInstance();
        }
        char symbol = getNextSymbol();
        IToken token;
        switch (symbol){
            case CONST_INT_START:
                token= onDeclarationDetected(ConstIntegerToken.class);
                break;
            case ARRAY_START_1:
            case ARRAY_START_2:
                token= onDeclarationDetected(ArrayNameToken.class);
                break;
            case IDENT_START_1:
            case IDENT_START_2:
                token= onDeclarationDetected(IdentToken.class);
                break;
            case PLEASE_START:
                token = onKeywordDetected(KEY_WORD_PLEASE);
                break;
            case DO_START:
                token = onKeywordDetected(KEY_WORD_DO);
                break;
            case FORGET_START:
                token = onKeywordDetected(KEY_WORD_FORGET);
                break;
            case END_SYMBOL:
                token = EndToken.getInstance();
                break;
            case NEW_LINE:
                pos.incLine();
                pos.setPos(0);
                return getNextToken();
            case SPACE:
            case TAB:
                return getNextToken();
            default:
                messageList.addError(pos,Messages.SYNTAX_ERROR);
                return getNextToken();
        }

        return token;
    }

    public void printMessage(){
        for (IMessage m : messageList.getSorted()){
            System.out.println(m);
        }
    }

    private char getNextSymbol(){
        return program.charAt(pos.goToNextSymbol());
    }

    private char peekNextSymbol(){
        return program.charAt(pos.getIndex());
    }

    private boolean finished(){
        return pos.getIndex()==program.length();
    }

    private IToken onDeclarationDetected(Class<?> clazz){
        Position startPos=new Position(pos);
        StringBuilder value=new StringBuilder();
        while (Character.isDigit(peekNextSymbol())) {
            value.append(getNextSymbol());
        }
        if (pos.equals(startPos)){
            String symbol = String.format("%s",getNextSymbol());
            startPos.goToNextSymbol();
            messageList.addError(startPos,format(Messages.UNEXPECTED_CHARACTER__0__1__EXPECTED,symbol,DECIMAL));
            return getNextToken();
        }

        Fragment fragment = new Fragment(startPos,pos);
        try {
            return (IToken) clazz.getConstructors()[0].newInstance(value.toString(),fragment);
        } catch (Exception e){
            logger.log(Level.SEVERE,format(Messages.FAILED_TO_CREATE_INSTANCE_OF__0,clazz.getName()));
            return null;
        }
    }

    private IToken onKeywordDetected(String expectedKeyword){
        StringBuilder value = new StringBuilder().append(expectedKeyword.charAt(0));
        Position startPos=new Position(pos);
        int i=1;
        char symbol;
        boolean flag=true;
        while (i<expectedKeyword.length() && Character.isLetter(symbol=getNextSymbol())){
            if (symbol != expectedKeyword.charAt(i)){
                flag = false;
            }
            value.append(symbol);
            i++;
        }
        if (flag){
            return new KeyWordToken(expectedKeyword,new Fragment(startPos,pos));
        }else{
            messageList.addError(startPos,format(Messages.WRONG_COMMAND__0__SUGGESTION__1,value.toString(),expectedKeyword));
            return getNextToken();
        }
    }
}