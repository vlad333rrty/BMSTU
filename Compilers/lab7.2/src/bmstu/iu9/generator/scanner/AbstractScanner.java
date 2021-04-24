package bmstu.iu9.generator.scanner;

import bmstu.iu9.generator.tokens.IToken;

public abstract class AbstractScanner {
    protected String program;

    public abstract IToken getNextToken();

    protected AbstractScanner(String program) {
        this.program = program;
    }
}
