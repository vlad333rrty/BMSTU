package bmstu.iu9.parser;

import bmstu.iu9.grammar.Grammar;
import bmstu.iu9.tokens.IToken;

import java.util.List;

public abstract class AbstractParser {
    protected final Grammar grammar;
    protected final List<IToken> program;

    public abstract ParseResult parse();

    protected AbstractParser(Grammar grammar, List<IToken> program){
        this.grammar = grammar;
        this.program = program;
    }
}
