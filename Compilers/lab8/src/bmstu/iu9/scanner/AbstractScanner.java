package bmstu.iu9.scanner;

import bmstu.iu9.tokens.IToken;

public abstract class AbstractScanner {
    protected String program;

    public abstract IToken getNextToken();

    protected AbstractScanner(String program) {
        this.program = program;
    }
}
