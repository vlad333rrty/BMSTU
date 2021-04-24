package bmstu.iu9.parser;

import static bmstu.iu9.format.FormatUtils.format;

import bmstu.iu9.grammar.*;
import bmstu.iu9.parser.syntax.tree.ISyntaxTree;
import bmstu.iu9.parser.syntax.tree.ISyntaxTreeNode;
import bmstu.iu9.parser.syntax.tree.SyntaxTree;
import bmstu.iu9.parser.syntax.tree.SyntaxTreeNodeFactory;
import bmstu.iu9.parser.syntax.tree.SyntaxTreeTerminalNode;
import bmstu.iu9.tokens.AbstractToken;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser extends AbstractParser {
    private static final String EPSILON = "epsilon";

    private final SyntaxTreeNodeFactory factory = new SyntaxTreeNodeFactory();
    private final Map<NonTerminal, Map<String, List<GObject>>> table = new HashMap<>(); // STRING <-> TERMINAL
    private final GrammarHandler grammarHandler;

    private int index = 0;

    public Parser(Grammar grammar, List<IToken> program) {
        super(grammar, program);
        grammarHandler = new GrammarHandler(grammar);
        buildTable();
    }

    @Override
    public ParseResult parse() {
        Stack<GObject> stack = new Stack<>();

        Terminal end = new Terminal(EndToken.getValue());
        NonTerminal start = new NonTerminal(grammar.getAxiomName());

        stack.push(end);
        stack.push(start);

        ISyntaxTreeNode root = factory.createNode(start);
        ISyntaxTree tree = new SyntaxTree(root);
        Stack<ISyntaxTreeNode> nodes = new Stack<>();
        nodes.push(factory.createNode(end));
        nodes.push(root);

        IToken token = getNextToken();
        GObject top;
        ISyntaxTreeNode node;
        do {
            String val = getValue(token);
            top = stack.pop();
            node = nodes.pop();
            if (top instanceof NonTerminal) {
                List<GObject> rule = table.get(top).get(val);
                if (rule == null || rule.isEmpty()) {
                    return new ParseResult(ParseStatus.FAILURE, tree,
                            format(Messages.UNEXPECTED_TOKEN__0, ((AbstractToken) token).getValue()));
                }
                //TODO get rid of 2 cycles

                for (GObject gObject : rule) {
                    ISyntaxTreeNode child = factory.createNode(gObject);
                    node.addChild(child);
                }

                for (int i = rule.size() - 1; i >= 0; i--) {
                    stack.push(rule.get(i));
                    nodes.push(node.getChildren().get(i));
                }
            } else {
                if (top.getValue().equals(EPSILON)) {
                    continue;
                }
                if (!top.getValue().equals(val)) {
                    return new ParseResult(ParseStatus.FAILURE, tree,
                            format(Messages.UNEXPECTED_TOKEN__0, ((AbstractToken) token).getValue()));
                }

                ((SyntaxTreeTerminalNode)node).setCorrespondingToken(token);
                token = getNextToken();
            }
        } while (!top.getValue().equals(EndToken.getValue()));

        return new ParseResult(ParseStatus.SUCCESS, tree, Messages.NO_WARNINGS);
    }

    private void buildTable() {
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            Map<String, List<GObject>> map = new HashMap<>();
            for (Terminal terminal : grammar.getTerminals()) {
                map.put(terminal.getValue(), new ArrayList<>());
            }
            table.put(nonTerminal, map);
        }
        for (Map.Entry<NonTerminal, List<List<GObject>>> entry : grammar.getRules().entrySet()) {
            final Map<String, List<GObject>> section = table.get(entry.getKey());

            AtomicInteger i = new AtomicInteger();
            for (Set<Terminal> first : grammarHandler.getFirst(entry.getKey())) {
                if (entry.getValue().get(i.get()).stream().anyMatch(t -> t.getValue().equals(EPSILON))) {
                    Set<Terminal> follow = grammarHandler.getFollow(entry.getKey());
                    follow.forEach(t -> section.get(t.getValue()).addAll(entry.getValue().get(i.getAndIncrement())));
                } else {
                    first.forEach(t -> section.get(t.getValue()).addAll(entry.getValue().get(i.getAndIncrement())));
                }
            }
        }
    }

    private IToken getNextToken() {
        if (index == program.size()) {
            return EndToken.getInstance();
        }
        return program.get(index++);
    }

    private String getValue(IToken token) {
        if (token instanceof AbstractToken) {
            return ((AbstractToken) token).getTag();
        } else {
            return EndToken.getValue();
        }
    }
}
