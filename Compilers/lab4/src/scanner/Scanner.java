package scanner;

import data.*;
import modules.AbstractScanner;
import tokens.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner extends AbstractScanner {
    private static final String CONST_INTEGER_GROUP_NAME="constInteger";
    private static final String IDENT_GROUP_NAME="ident";
    private static final String ARRAY_NAME_GROUP_NAME="arrayName";
    private static final String KEY_WORD_GROUP_NAME="keyWord";
    private static final String END_TOKEN_GROUP_NAME="end";
    private static final String WHITESPACE_GROUP_NAME ="whitespace";
    private static final String ERROR_MESSAGE="syntax error";
    private static final String END_CHAR="!";

    private final IMessageList messageList=new MessageList();
    private final INameDictionary nameDictionary=new NameDictionary();

    private final Matcher matcher;

    private int lastMatchEnd,lastMatchStart,line=1,pos=1;
    private boolean finished;

    public Scanner(String program) {
        super(program.replaceAll("\n+$", "").concat(END_CHAR));
        String constIntegerRegex = "#[0-9]+";
        String identRegex = "(\\.|:)[0-9]+";
        String arrayNameRegex = "(,|;)[0-9]+";
        String keyWordRegex = "PLEASE|DO|FORGET";
        String whitespaceRegex="\\s";
        Pattern pattern = Pattern.compile(String.format("(?<%s>%s)|(?<%s>%s)|(?<%s>%s)|(?<%s>%s)|(?<%s>%s)|(?<%s>%s)",
                CONST_INTEGER_GROUP_NAME, constIntegerRegex, IDENT_GROUP_NAME, identRegex, ARRAY_NAME_GROUP_NAME,
                arrayNameRegex, KEY_WORD_GROUP_NAME, keyWordRegex, WHITESPACE_GROUP_NAME,whitespaceRegex,
                END_TOKEN_GROUP_NAME, END_CHAR));
        matcher = pattern.matcher(this.program);
    }

    @Override
    public AbstractToken getNextToken() {
        if (finished) {
            return null;
        }
        if (matcher.find()) {
            if (isSyntaxError()) {
                updatePos(lastMatchStart,lastMatchEnd);
                messageList.addError(new Position(line, pos, lastMatchEnd), ERROR_MESSAGE);
                updatePos(lastMatchEnd,matcher.start());
            }else{
                updatePos(lastMatchStart, matcher.start());
            }
            lastMatchStart = matcher.start();
            lastMatchEnd = matcher.end();
            return getToken();
        } else {
            throw new IllegalStateException("Unexpected input"); //Just in case
        }
    }

    public void printMessageList(){
        for (IMessage message:messageList.getSorted()){
            System.out.println(message);
        }
    }

    private void updatePos(int startPos, int endPos) {
        for (int i = startPos; i < endPos; i++) {
            if (program.charAt(i) == '\n') {
                line++;
                pos = 1;
            } else {
                pos++;
            }
        }
    }

    private boolean isSyntaxError() {
        return lastMatchEnd < matcher.start();
    }

    private AbstractToken getToken() {
        if (matcher.group(WHITESPACE_GROUP_NAME) != null) {
            return getNextToken();
        }
        if (matcher.group(END_TOKEN_GROUP_NAME) != null) {
            finished = true;
            return null;
        }

        int end = matcher.end() - matcher.start() + pos;
        Position start = new Position(line, pos, matcher.start());
        Position follow = new Position(line, end, matcher.end());

        if (matcher.group(CONST_INTEGER_GROUP_NAME) != null) {
            nameDictionary.addName(matcher.group(CONST_INTEGER_GROUP_NAME));
            return new ConstIntegerToken(matcher.group(CONST_INTEGER_GROUP_NAME), new Fragment(start, follow));
        }
        if (matcher.group(IDENT_GROUP_NAME) != null) {
            nameDictionary.addName(matcher.group(IDENT_GROUP_NAME));
            return new IdentToken(matcher.group(IDENT_GROUP_NAME), new Fragment(start, follow));
        }
        if (matcher.group(ARRAY_NAME_GROUP_NAME) != null) {
            nameDictionary.addName(matcher.group(ARRAY_NAME_GROUP_NAME));
            return new ArrayNameToken(matcher.group(ARRAY_NAME_GROUP_NAME), new Fragment(start, follow));
        }
        if (matcher.group(KEY_WORD_GROUP_NAME) != null) {
            return new KeyWordToken(matcher.group(KEY_WORD_GROUP_NAME), new Fragment(start, follow));
        }
        throw new IllegalStateException("Unknown token type");
    }
}
