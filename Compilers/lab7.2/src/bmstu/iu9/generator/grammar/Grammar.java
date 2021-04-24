package bmstu.iu9.generator.grammar;

import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static bmstu.iu9.utils.Utils.getTokenValue;

public class Grammar implements IGrammar {
    private static final String DEFAULT_AXIOM_NAME = "S";
    private static final String EPSILON = "@";

    private final Map<NonTerminal, List<List<GObject>>> rules = new HashMap<>();
    private final Set<Terminal> terminals = new HashSet<>();
    private final Set<NonTerminal> nonTerminals = new HashSet<>();

    private final Map<String, GObject> stringToGObject = new HashMap<>();
    private Axiom axiom;

    public Grammar(List<IToken> program) {
        init(program);
    }

    @Override
    public String getAxiomName() {
        return DEFAULT_AXIOM_NAME;
    }

    @Override
    public String getEpsilonName() {
        return EPSILON;
    }

    @Override
    public Map<NonTerminal, List<List<GObject>>> getRules() {
        return rules;
    }

    @Override
    public Set<Terminal> getTerminals() {
        return terminals;
    }

    @Override
    public Set<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }

    private void init(List<IToken> program) {
        Iterator<IToken> it = program.listIterator();
        it.next(); // {
        nonTerminals.add(new Axiom(getTokenValue(it.next())));
        it.next(); // }
        while (getTokenValue(it.next()).equals(",")) {
            nonTerminals.add(new NonTerminal(getTokenValue(it.next())));
        }


        IToken token = it.next();
        while (it.hasNext() && !getTokenValue(token).equals(EndToken.getValue())) {
            NonTerminal current = new NonTerminal(getTokenValue(token));
            rules.put(current, new ArrayList<>());
            token = it.next();
            while (!getTokenValue(token).equals("]")) {
                if (getTokenValue(token).equals(":")) {
                    rules.get(current).add(new ArrayList<>());
                    token = it.next();
                }
                NonTerminal nextNonTerminal = new NonTerminal(getTokenValue(token));
                if (nonTerminals.contains(nextNonTerminal)) {
                    var list = rules.get(current);
                    list.get(list.size() - 1).add(nextNonTerminal);
                } else {
                    Terminal terminal = new Terminal(getTokenValue(token));
                    terminals.add(terminal);
                    var list = rules.get(current);
                    list.get(list.size() - 1).add(terminal);
                }
                token = it.next();
            }
            token = it.next();
            if (getTokenValue(token).equals(EndToken.getValue())) {
                break;
            }
            token = it.next();
        }
        terminals.add(new Terminal(EndToken.getValue()));
    }
}
