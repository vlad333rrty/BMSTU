package bmstu.iu9.calculator;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.modules.Compiler;
import bmstu.iu9.generator.modules.ICompiler;
import bmstu.iu9.generator.parser.AbstractTopDownParser;
import bmstu.iu9.generator.parser.CalculatorParser;
import bmstu.iu9.generator.parser.ParseResult;
import bmstu.iu9.generator.scanner.AbstractScanner;
import bmstu.iu9.generator.syntaxtree.ISyntaxTreeNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeNonTerminalNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeTerminalNode;
import bmstu.iu9.generator.tokens.AdditiveOperationToken;
import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;
import bmstu.iu9.generator.tokens.MultiplicativeOperationToken;
import bmstu.iu9.generator.tokens.NumberToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bmstu.iu9.generator.table.TableConstants.getCommon;
import static bmstu.iu9.generator.table.TableConstants.getNonTerm;
import static bmstu.iu9.generator.table.TableConstants.getTerm;
import static bmstu.iu9.utils.SerializationUtils.deserializeAxiomName;
import static bmstu.iu9.utils.SerializationUtils.deserializeEpsilonName;

public class Calculator {
    private static final int TRANSIT_RULE_LENGTH = 2;
    private static final int EXPR_RULE_LENGTH = 3;

    private static int[][][] TABLE = new int[][][]{
            {{},{5, },{},{8, 2, 0, },{},{5, },{},},
            {{5, },{5, },{},{5, },{9, 4, 1, },{5, },{},},
            {{},{},{4, 1, },{},{},{},{4, 1, },},
            {{},{},{2, 0, },{},{},{},{2, 0, },},
            {{},{},{7, },{},{},{},{11, 3, 10, },},
    };

    static String x = "{NonTerminal: E'={@=[], $=[Terminal: @], \"n\"=[], \"+\"=[Terminal: \"+\", NonTerminal: T, NonTerminal: E'], \"*\"=[], \")\"=[Terminal: @], \"(\"=[]}," +
            " NonTerminal: T'={@=[Terminal: @], $=[Terminal: @], \"n\"=[], \"+\"=[Terminal: @], \"*\"=[Terminal: \"*\", NonTerminal: F, NonTerminal: T'], \")\"=[Terminal: @], \"(\"=[]}," +
            " NonTerminal: T={@=[], $=[], \"n\"=[NonTerminal: F, NonTerminal: T'], \"+\"=[], \"*\"=[], \")\"=[], \"(\"=[NonTerminal: F, NonTerminal: T']}," +
            " NonTerminal: E={@=[], $=[], \"n\"=[NonTerminal: T, NonTerminal: E'], \"+\"=[], \"*\"=[], \")\"=[], \"(\"=[NonTerminal: T, NonTerminal: E']}, " +
            "NonTerminal: F={@=[], $=[], \"n\"=[Terminal: \"n\"], \"+\"=[], \"*\"=[], \")\"=[], \"(\"=[Terminal: \"(\", NonTerminal: E, Terminal: \")\"]}}\n";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String program = "\"3\"\"*\"\"3\"\"+\"\"2\"\"*\"\"(\"\"2\"\"+\"\"2\"\")\"";
        ICompiler compiler = new Compiler();
        AbstractScanner sc = compiler.getScanner(program);

        List<IToken> tokenStream = new ArrayList<>();

        for (IToken token = sc.getNextToken(); token != EndToken.getInstance(); token = sc.getNextToken()) {
            tokenStream.add(token);
        }
        tokenStream.add(EndToken.getInstance());

        Map<NonTerminal, Map<String, List<GObject>>> table = toTable();

        System.out.println(table);

        AbstractTopDownParser parser = new CalculatorParser();
        ParseResult result = parser.parse(table, tokenStream, deserializeAxiomName(), deserializeEpsilonName());
        if (result.getStatus() == Status.FAILURE) {
            System.err.println(result.getMessage());
            System.exit(-1);
        }
        System.out.println(result.getMessage());
        System.out.println(result.getSyntaxTree());

        System.out.println(calculate(result.getSyntaxTree().getRoot()));
    }

    private static int calculate(ISyntaxTreeNode node) {
        if (node instanceof SyntaxTreeTerminalNode) {
            IToken token = ((SyntaxTreeTerminalNode) node).getCorrespondingToken();
            if (token instanceof NumberToken) {
                return Integer.parseInt(removeQuotes(((NumberToken) token).getValue()));
            }
        } else {
            if (node.getChildren().size() == TRANSIT_RULE_LENGTH) {
                ISyntaxTreeNode first = node.getChildren().get(0);
                ISyntaxTreeNode second = node.getChildren().get(1);
                if (first instanceof SyntaxTreeNonTerminalNode
                        && second instanceof SyntaxTreeNonTerminalNode) {
                    if (second.getChildren().get(0) instanceof SyntaxTreeTerminalNode) {
                        IToken token = ((SyntaxTreeTerminalNode) second.getChildren().get(0)).getCorrespondingToken();
                        if (token instanceof AdditiveOperationToken) {
                            return calculate(first) + calculate(second);
                        } else if (token instanceof MultiplicativeOperationToken) {
                            return calculate(first) * calculate(second);
                        }
                        return calculate(first);
                    }
                }
            } else if (node.getChildren().size() == EXPR_RULE_LENGTH) {
                ISyntaxTreeNode first = node.getChildren().get(1);
                ISyntaxTreeNode second = node.getChildren().get(2);
                if (first instanceof SyntaxTreeNonTerminalNode
                        && second instanceof SyntaxTreeNonTerminalNode) {
                    if (second.getChildren().get(0) instanceof SyntaxTreeTerminalNode) {
                        IToken token = ((SyntaxTreeTerminalNode) second.getChildren().get(0)).getCorrespondingToken();
                        if (token instanceof AdditiveOperationToken) {
                            return calculate(first) + calculate(second);
                        } else if (token instanceof MultiplicativeOperationToken) {
                            return calculate(first) * calculate(second);
                        }
                        return calculate(first);
                    }
                }
                return calculate(first);
            }
        }

        return calculate(node.getChildren().get(0));
    }

    private static String removeQuotes(String str) {
        return str.substring(1, str.length() - 1);
    }

    private static Map<NonTerminal, Map<String, List<GObject>>> toTable(){
        Map<NonTerminal, Map<String, List<GObject>>> table1 = new HashMap<>();
        for (int i = 0; i< TABLE.length; i++){
            NonTerminal nonTerminal = new NonTerminal(getNonTerm(i));
            table1.put(nonTerminal,new HashMap<>());
            for (int j = 0; j< TABLE[i].length; j++){
                table1.get(nonTerminal).put(getTerm(j),new ArrayList<>());
                for (int k = 0; k< TABLE[i][j].length; k++){
                    int x = TABLE[i][j][k];
                    String name = getCommon(TABLE[i][j][k]);
                    table1.get(nonTerminal).get(getTerm(j)).add(x>4 ? new Terminal(name) : new NonTerminal(name));
                }
            }
        }
        return table1;
    }
}
