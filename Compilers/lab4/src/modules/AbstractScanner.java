package modules;

import tokens.IToken;

public abstract class AbstractScanner {
    protected String program;

    public abstract IToken getNextToken();

    protected AbstractScanner(String program){
        this.program=program;
    }
}
