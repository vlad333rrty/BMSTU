package bmstu.iu9.grammar;

import bmstu.iu9.tokens.EndToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GrammarHandler {
    private final IGrammar grammar;
    private final Map<NonTerminal, List<Set<Terminal>>> first = new HashMap<>();
    private final Map<NonTerminal, Set<Terminal>> follow = new HashMap<>();
    private Map<NonTerminal, Set<Terminal>> flatFirst = new HashMap<>();

    private final Map<NonTerminal, Set<Terminal>> firstSet = new HashMap<>();

    public GrammarHandler(IGrammar grammar) {
        this.grammar = grammar;
    }

    public List<Set<Terminal>> getFirst(NonTerminal x) {
        if (first.isEmpty()) {
            buildFirst();
        }
        return first.get(x);
    }

    public Set<Terminal> getFlatFirst(NonTerminal x) {
        if (flatFirst.isEmpty()) {
            buildFlatFirst();
        }
        return flatFirst.get(x);
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

    private void buildFlatFirst(){
        if (first.isEmpty()) {
            buildFirst();
        }
        Map<NonTerminal,Set<Terminal>> newFirst = new HashMap<>();
        for (var entry : first.entrySet()){
            newFirst.put(entry.getKey(),new HashSet<>());
            for (Set<Terminal> set:entry.getValue()){
                newFirst.get(entry.getKey()).addAll(set);
            }
        }
        flatFirst = newFirst;
    }

    private void buildFirst() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<NonTerminal, List<List<GObject>>> entry : grammar.getRules().entrySet()) {
                List<Set<Terminal>> terminals = first.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                Set<Terminal> terminalS = firstSet.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
                int len = terminalS.size();
                for (List<GObject> rule : entry.getValue()) {
                    GObject gObject;
                    if (rule.isEmpty()){
                        gObject = new Terminal(grammar.getEpsilonName());
                    }else{
                        gObject = rule.get(0);
                    }
                    if (terminalS.addAll(getFirst(gObject))) {
                        terminals.add(new HashSet<>(getFirst(gObject)));
                    }
                }
                if (terminalS.size() != len) {
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
            List<Set<Terminal>> set = this.first.get(gObject);
            if (set != null) {
                for (Set<Terminal> s : set) {
                    first.addAll(s);
                }
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
                            Set<Terminal> first = getFirst(rule.get(i + 1));
                            if (first.isEmpty() || first.stream().anyMatch(t -> t.value.equals(grammar.getEpsilonName()))) {
                                follow.addAll(this.follow.get(entry.getKey()));
                            } else {
                                follow.addAll(first);
                            }
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
