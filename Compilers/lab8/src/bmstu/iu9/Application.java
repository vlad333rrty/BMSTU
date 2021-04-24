package bmstu.iu9;

import bmstu.iu9.data.Status;
import bmstu.iu9.grammar.GrammarHandler;
import bmstu.iu9.io.FileUtils;
import bmstu.iu9.modules.Compiler;
import bmstu.iu9.modules.ICompiler;
import bmstu.iu9.parser.IParser;
import bmstu.iu9.parser.ParseResult;
import bmstu.iu9.parser.RecursiveDescentParser;
import bmstu.iu9.scanner.AbstractScanner;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

import java.util.ArrayList;
import java.util.List;

public final class Application {
    public void start(String fileName, String grammarFileName) {
        String program = FileUtils.readFile(fileName);
        ICompiler compiler = new Compiler();
        AbstractScanner scanner = compiler.getScanner(program);
        List<IToken> tokenStream = new ArrayList<>();

        for (IToken token = scanner.getNextToken(); token != EndToken.getInstance(); token = scanner.getNextToken()) {
            tokenStream.add(token);
        }
        tokenStream.add(EndToken.getInstance());

        IParser parser = new RecursiveDescentParser(tokenStream);
        ParseResult result = parser.parse();
        System.out.println(result.getMessage());
        if (result.getStatus() == Status.FAILURE) {
            System.exit(-1);
        }

        GrammarHandler grammarHandler = new GrammarHandler(result.getConstructedGrammar());

        System.out.println("FIRST:");
        for (var e : result.getConstructedGrammar().getRules().entrySet()) {
            System.out.printf("%s = {%s}\n", e.getKey(), grammarHandler.getFlatFirst(e.getKey()));
        }
    }
}
