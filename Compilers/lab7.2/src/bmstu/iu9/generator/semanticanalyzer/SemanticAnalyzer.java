package bmstu.iu9.generator.semanticanalyzer;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.grammar.Axiom;
import bmstu.iu9.generator.grammar.CalcGrammar;
import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.syntaxtree.ISyntaxTree;
import bmstu.iu9.generator.syntaxtree.ISyntaxTreeNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeTerminalNode;
import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static bmstu.iu9.generator.format.FormatUtils.format;
import static bmstu.iu9.utils.Utils.getTokenTag;
import static bmstu.iu9.utils.Utils.getTokenValue;

public class SemanticAnalyzer implements ISemanticAnalyzer {
    private static final String AXIOM_DECLARATION_START = "\"LEFT_BRACE\"";
    private static final String AXIOM_DECLARATION_FINISH = "\"RIGHT_BRACE\"";
    private static final String DELIMITER = "\"COMMA\"";
    private static final String RULE_DELIMITER = "COLON";
    private static final String NON_TERMINAL_TAG = "NON_TERMINAL";
    private static final int NON_TERMINAL_PLACE_IN_DECLARATION = 1;

    private final ISyntaxTree syntaxTree;

    private final Set<NonTerminal> declarations = new HashSet<>();
    private final Set<Terminal> terminals = new HashSet<>();
    private final Map<NonTerminal, List<List<GObject>>> rules = new HashMap<>();
    private Axiom axiom;

    public SemanticAnalyzer(ISyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    @Override
    public SemanticAnalysisResult analyze() {
        try {
            ISyntaxTreeNode node = collectDeclarations(syntaxTree.getRoot());
            collectRules(node);
            terminals.add(new Terminal(EndToken.getValue()));
        } catch (SemanticAnalysisException e) {
            return new SemanticAnalysisResult(Status.FAILURE, null, e.getMessage());
        }

        return new SemanticAnalysisResult(Status.SUCCESS, new CalcGrammar(rules, declarations, terminals, axiom),
                Messages.NO_WARNINGS);
    }


    private ISyntaxTreeNode collectDeclarations(ISyntaxTreeNode node) throws SemanticAnalysisException {
        if (node.getChildren().get(0).getValue().getValue().equals(AXIOM_DECLARATION_START)) {
            node = collectAxiom(node);
        }

        while (node.getChildren().get(0).getValue().getValue().equals(DELIMITER)) {
            String value = getTokenValue(((SyntaxTreeTerminalNode) node.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION))
                    .getCorrespondingToken());
            if (!declarations.add(new NonTerminal(value))) {
                throw new SemanticAnalysisException(format(Messages.MULTIPLE_NON_TERMINAL__0__DECLARATIONS, value));
            }
            node = node.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION + 1);
        }

        return node;
    }

    private ISyntaxTreeNode collectAxiom(ISyntaxTreeNode node) {
        axiom = new Axiom(node.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION).getValue().getValue());
        declarations.add(axiom);
        ISyntaxTreeNode nextNode = node.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION);
        while (!nextNode.getChildren().get(0).getValue().getValue().equals(AXIOM_DECLARATION_FINISH)) {
            nextNode = nextNode.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION);
        }
        nextNode = nextNode.getChildren().get(NON_TERMINAL_PLACE_IN_DECLARATION);

        return nextNode;
    }

    private void collectRules(ISyntaxTreeNode node) throws SemanticAnalysisException {
        collectRule(node.getChildren().get(1));
    }

    private void collectRule(ISyntaxTreeNode node) throws SemanticAnalysisException {
        String value = getTokenValue(((SyntaxTreeTerminalNode) node.getChildren().get(0))
                .getCorrespondingToken());
        NonTerminal nonTerminal = new NonTerminal(value);
        if (declarations.add(nonTerminal)) {
            throw new SemanticAnalysisException(format(Messages.UNKNOWN_NON_TERMINAL__0, value));
        }
        rules.put(nonTerminal, new ArrayList<>());

        node = node.getChildren().get(1);

        collectRulePart(node.getChildren().get(1), nonTerminal);
        closeRule(node.getChildren().get(2));
    }

    private void collectRulePart(ISyntaxTreeNode node, NonTerminal left) throws SemanticAnalysisException {
        List<List<GObject>> rule = rules.get(left);
        rule.add(new ArrayList<>());

        IToken token = ((SyntaxTreeTerminalNode) node.getChildren().get(0)).getCorrespondingToken();

        String tag = token == null ? null : getTokenTag(token);
        String value = tag != null ? getTokenValue(token) : null;
        while (tag != null && !tag.equals(RULE_DELIMITER)) {
            if (tag.equals(NON_TERMINAL_TAG)) {
                NonTerminal nonTerminal = new NonTerminal(value);
                if (declarations.add(nonTerminal)) {
                    throw new SemanticAnalysisException(format(Messages.UNKNOWN_NON_TERMINAL__0, value));
                }
                rule.get(rule.size() - 1).add(nonTerminal);
            } else {
                Terminal terminal = new Terminal(value);
                rule.get(rule.size() - 1).add(terminal);
                terminals.add(terminal);
            }

            node = node.getChildren().get(1);
            token = ((SyntaxTreeTerminalNode) node.getChildren().get(0)).getCorrespondingToken();
            tag = token == null ? null : getTokenTag(token);
            value = tag != null ? getTokenValue(token) : null;
        }

        if (tag != null) {
            collectRulePart(node.getChildren().get(1), left);
        }
    }

    private void closeRule(ISyntaxTreeNode node) throws SemanticAnalysisException {
        node = node.getChildren().get(1);
        IToken token = ((SyntaxTreeTerminalNode) node.getChildren().get(0)).getCorrespondingToken();
        if (token != null) {
            collectRule(node.getChildren().get(1));
        }
    }
}
