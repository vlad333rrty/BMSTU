public class Token {
    public enum TokenType{
        IDENT,NUMBER,STRING,ERROR,END
    }

    private static final Token endToken=new Token();
    private final String value;
    private final TokenType type;
    private final int start,end;

    public Token(String value,TokenType type,int start,int end) {
        this.value = value;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public static Token getEndToken(){
        return endToken;
    }

    @Override
    public String toString() {
        switch (type) {
            case IDENT:
                return String.format("%s (%d,%d): %s", TokenType.IDENT.toString(), start, end, value);
            case NUMBER:
                return String.format("%s (%d,%d): %s", TokenType.NUMBER.toString(), start, end, value);
            case STRING:
                return String.format("%s (%d,%d): %s", TokenType.STRING.toString(), start, end,
                        value.replaceAll("\\\\\n",""));
            case ERROR:
                return String.format("%s (%d,%d)", value, start, end);
            case END:
                return TokenType.END.toString();
            default:
                throw new IllegalArgumentException("Unsupported type");
        }
    }

    public TokenType getType() {
        return type;
    }

    private Token() {
        this.value = null;
        this.type = TokenType.END;
        this.start = 0;
        this.end = 0;
    }
}
