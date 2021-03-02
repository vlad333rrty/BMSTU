package modules;

import tokens.AbstractToken;

public abstract class AbstractScanner {
    protected String program;

    public abstract AbstractToken getNextToken();

    protected AbstractScanner(String program){
        this.program=program;
    }
}
