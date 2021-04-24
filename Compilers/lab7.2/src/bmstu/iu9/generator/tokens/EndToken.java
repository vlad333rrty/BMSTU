package bmstu.iu9.generator.tokens;

public class EndToken implements IToken {
    private static final IToken END_TOKEN = new EndToken();

    public static IToken getInstance() {
        return END_TOKEN;
    }

    public static String getValue() {
        return "$";
    }

    @Override
    public String toString() {
        return "END";
    }

    private EndToken() {

    }
}
