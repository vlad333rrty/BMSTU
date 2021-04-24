package bmstu.iu9.grammar;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class CalcGrammar implements IGrammar{
    private final Map<NonTerminal, List<List<GObject>>> rules;
    private final Set<NonTerminal> nonTerminals;
    private final Set<Terminal> terminals;
    private final Axiom axiom;

    public CalcGrammar(Map<NonTerminal, List<List<GObject>>> rules, Set<NonTerminal> nonTerminals,
                       Set<Terminal> terminals, Axiom axiom) {
        this.rules = rules;
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.axiom = axiom;
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

    @Override
    public String getAxiomName() {
        return axiom.getValue();
    }

    @Override
    public String getEpsilonName() {
        return "@";
    }
}
