package bmstu.iu9.grammar;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IGrammar {
    Map<NonTerminal, List<List<GObject>>> getRules();

    Set<Terminal> getTerminals();

    Set<NonTerminal> getNonTerminals();

    String getAxiomName();

    String getEpsilonName();
}
