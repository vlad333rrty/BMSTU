package bmstu.iu9.modules;

import bmstu.iu9.grammar.Grammar;
import bmstu.iu9.parser.AbstractParser;
import bmstu.iu9.modules.ICompiler;
import bmstu.iu9.parser.Parser;
import bmstu.iu9.scanner.AbstractScanner;
import bmstu.iu9.scanner.Scanner;
import bmstu.iu9.tokens.IToken;

import java.util.List;

public class Compiler implements ICompiler {

    @Override
    public AbstractScanner getScanner(String program) {
        return new Scanner(program);
    }

    @Override
    public AbstractParser getParser(Grammar grammar, List<IToken> program) {
        return new Parser(grammar,program);
    }
}
