package bmstu.iu9.generator;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.grammar.Grammar;
import bmstu.iu9.generator.io.FileUtils;
import bmstu.iu9.generator.modules.Compiler;
import bmstu.iu9.generator.modules.ICompiler;
import bmstu.iu9.generator.parser.ParseResult;
import bmstu.iu9.generator.parser.ParseUtils;
import bmstu.iu9.generator.scanner.AbstractScanner;
import bmstu.iu9.generator.semanticanalyzer.SemanticAnalysisResult;
import bmstu.iu9.generator.semanticanalyzer.SemanticAnalyzer;
import bmstu.iu9.generator.table.TableSerializer;
import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;
import bmstu.iu9.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private final ICompiler compiler = new Compiler();

    public void start(String fileName, String grammarFileName) {
        List<IToken> tokenStream = getTokens(fileName);
        List<IToken> stream = getTokens(grammarFileName);
        ParseResult result = ParseUtils.parse(new Grammar(stream), tokenStream);

        if (result.getStatus() == Status.FAILURE) {
            System.err.println(result.getMessage());
            System.exit(-1);
        }
        System.out.println(result.getMessage());
        System.out.println(result.getSyntaxTree());

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(result.getSyntaxTree());
        SemanticAnalysisResult sr = semanticAnalyzer.analyze();
        System.out.println(sr.getMessage());

        var table = ParseUtils.buildParseTable(sr.getConstructedGrammar());

        SerializationUtils.serialize(table,
                sr.getConstructedGrammar().getAxiomName(), sr.getConstructedGrammar().getEpsilonName());

        System.out.println("FINISHED SUCCESSFULLY");
        System.out.println(table);
        TableSerializer serializer = new TableSerializer(table);
        serializer.serialize();
    }

    private List<IToken> getTokens(String fileName) {
        String program = FileUtils.readFile(fileName);
        AbstractScanner scanner = compiler.getScanner(program);

        List<IToken> tokenStream = new ArrayList<>();
        for (IToken token = scanner.getNextToken(); token != EndToken.getInstance(); token = scanner.getNextToken()) {
            tokenStream.add(token);
        }
        tokenStream.add(EndToken.getInstance());

        return tokenStream;
    }
}
