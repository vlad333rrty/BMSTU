package bmstu.iu9;

import bmstu.iu9.parser.ParseStatus;
import bmstu.iu9.grammar.Grammar;
import bmstu.iu9.io.FileUtils;
import bmstu.iu9.parser.AbstractParser;
import bmstu.iu9.scanner.AbstractScanner;
import bmstu.iu9.modules.ICompiler;
import bmstu.iu9.modules.Compiler;
import bmstu.iu9.parser.ParseResult;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

import java.util.ArrayList;
import java.util.List;

public final class Application {
    public void start(String fileName,String grammarFileName){
        String program= FileUtils.readFile(fileName);
        ICompiler compiler = new Compiler();
        AbstractScanner scanner=compiler.getScanner(program);
        List<IToken> tokenStream = new ArrayList<>();

        for (IToken token = scanner.getNextToken(); token!= EndToken.getInstance(); token=scanner.getNextToken()){
            tokenStream.add(token);
            //System.out.println(token);
        }
        tokenStream.add(EndToken.getInstance());

        AbstractParser parser = compiler.getParser(new Grammar(grammarFileName),tokenStream);
        ParseResult result = parser.parse();
        if (result.getStatus() == ParseStatus.SUCCESS){
            System.out.println(result.getSyntaxTree());
        }
        System.out.println(result.getMessage());
    }
}
