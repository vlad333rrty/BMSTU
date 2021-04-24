package bmstu.iu9.modules;

import bmstu.iu9.grammar.Grammar;
import bmstu.iu9.parser.AbstractParser;
import bmstu.iu9.scanner.AbstractScanner;
import bmstu.iu9.tokens.IToken;

import java.util.List;

public interface ICompiler {
    AbstractScanner getScanner(String program);
    AbstractParser getParser(Grammar grammar, List<IToken> program);
}
