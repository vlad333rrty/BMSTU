package tokens;

public class EndToken implements IToken{
    private static final IToken endToken=new EndToken();

    public static IToken getInstance(){
        return endToken;
    }

    @Override
    public String toString() {
        return "END";
    }

    private EndToken(){

    }
}
