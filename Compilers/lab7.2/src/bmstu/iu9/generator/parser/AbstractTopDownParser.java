package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.grammar.Axiom;
import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.syntaxtree.ISyntaxTree;
import bmstu.iu9.generator.syntaxtree.ISyntaxTreeNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTree;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeNodeFactory;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeTerminalNode;
import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import static bmstu.iu9.generator.format.FormatUtils.format;
import static bmstu.iu9.utils.Utils.getTokenValue;

public abstract class AbstractTopDownParser implements IParser {
    private final SyntaxTreeNodeFactory factory = new SyntaxTreeNodeFactory();
    private int index = 0;

    @Override
    public ParseResult parse(Map<NonTerminal, Map<String, List<GObject>>> table, List<IToken> program,
                             String axiomName, String epsilonName) {
        Stack<GObject> stack = new Stack<>();

        Terminal end = new Terminal(EndToken.getValue());
        Axiom start = new Axiom(axiomName);

        stack.push(end);
        stack.push(start);

        ISyntaxTreeNode root = factory.createNode(start);
        ISyntaxTree tree = new SyntaxTree(root);
        Stack<ISyntaxTreeNode> nodes = new Stack<>();
        nodes.push(factory.createNode(end));
        nodes.push(root);

        IToken token = getNextToken(program);
        GObject top;
        ISyntaxTreeNode node;
        do {
            top = stack.pop();
            node = nodes.pop();
            if (top instanceof NonTerminal) {
                String val = getTokenRepresentation(token);
                List<GObject> rule = table.get(top).get(val);
                if (rule == null || rule.isEmpty()) {
                    return new ParseResult(Status.FAILURE, tree,
                            format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                }

                for (GObject gObject : rule) {
                    ISyntaxTreeNode child = factory.createNode(gObject);
                    node.addChild(child);
                }

                for (int i = rule.size() - 1; i >= 0; i--) {
                    stack.push(rule.get(i));
                    nodes.push(node.getChildren().get(i));
                }
            } else {
                if (top.getValue().equals(epsilonName)) {
                    continue;
                }
                if (!areEqual((Terminal) top, token)) {
                    return new ParseResult(Status.FAILURE, tree,
                            format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                }

                ((SyntaxTreeTerminalNode) node).setCorrespondingToken(token);
                token = getNextToken(program);
            }
        } while (!top.getValue().equals(EndToken.getValue()));

        return new ParseResult(Status.SUCCESS, tree, Messages.NO_WARNINGS);
    }

    protected abstract String getTokenRepresentation(IToken token);

    protected abstract boolean areEqual(Terminal terminal, IToken token);

    private IToken getNextToken(List<IToken> program) {
        if (index == program.size()) {
            return EndToken.getInstance();
        }
        return program.get(index++);
    }
}
