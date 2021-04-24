package bmstu.iu9.generator.grammar;

import bmstu.iu9.generator.tokens.EndToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GrammarHandler1D {
    private final Grammar grammar;
    private final Map<NonTerminal, Set<Terminal>> first = new HashMap<>();
    private final Map<NonTerminal, Set<Terminal>> follow = new HashMap<>();

    public GrammarHandler1D(Grammar grammar) {
        this.grammar = grammar;
    }

    public Set<Terminal> getFirst(NonTerminal x) {
        if (first.isEmpty()) {
            buildFirst();
        }
        return first.get(x);
    }

    public Set<Terminal> getFollow(NonTerminal x) {
        if (first.isEmpty()) {
            buildFirst();
        }
        if (follow.isEmpty()) {
            buildFollow();
        }
        return follow.get(x);
    }

    private void buildFirst() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<NonTerminal, List<List<GObject>>> entry : grammar.getRules().entrySet()) {
                Set<Terminal> terminals = first.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
                int len = terminals.size();
                for (List<GObject> rule : entry.getValue()) {
                    GObject gObject = rule.get(0);
                    terminals.addAll(getFirst(gObject));
                }
                if (terminals.size() != len) {
                    changed = true;
                }
            }
        }
    }

    private Set<Terminal> getFirst(GObject gObject) {
        Set<Terminal> first = new HashSet<>();
        if (gObject instanceof Terminal) {
            first.add((Terminal) gObject);
        } else {
            Set<Terminal> set = this.first.get(gObject);
            if (set != null) {
                first.addAll(set);
            }
        }
        return first;
    }

    private void buildFollow() {
        for (Map.Entry<NonTerminal, List<List<GObject>>> entry : grammar.getRules().entrySet()) {
            Set<Terminal> set = new HashSet<>();
            if (entry.getKey().getValue().equals(grammar.getAxiomName())) {
                set.add(new Terminal(EndToken.getValue()));
            }
            follow.put(entry.getKey(), set);
        }
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<NonTerminal, List<List<GObject>>> entry : grammar.getRules().entrySet()) {
                for (List<GObject> rule : entry.getValue()) {
                    for (int i = 0; i < rule.size(); i++) {
                        if (!(rule.get(i) instanceof NonTerminal)) {
                            continue;
                        }
                        Set<Terminal> follow = this.follow.get(rule.get(i));
                        int len = follow.size();
                        if (i < rule.size() - 1) {
                            follow.addAll(getFirst(rule.get(i + 1)));
                        } else {
                            follow.addAll(this.follow.get(entry.getKey()));
                        }
                        if (follow.size() != len) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }
}
