package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.GrammarHandler;
import bmstu.iu9.generator.grammar.IGrammar;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.tokens.IToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ParseUtils {
    public static ParseResult parse(IGrammar grammar, List<IToken> program) {
        Map<NonTerminal, Map<String, List<GObject>>> table = buildParseTable(grammar);
        return new Parser().parse(table, program, grammar.getAxiomName(), grammar.getEpsilonName());
    }

    public static Map<NonTerminal, Map<String, List<GObject>>> buildParseTable(IGrammar grammar) {
        Map<NonTerminal, Map<String, List<GObject>>> table = new HashMap<>();
        GrammarHandler grammarHandler = new GrammarHandler(grammar);
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
                if (entry.getValue().get(i.get()).stream().anyMatch(t -> t.getValue().equals(grammar.getEpsilonName()))) { // seeking epsilon
                    Set<Terminal> follow = grammarHandler.getFollow(entry.getKey());
                    follow.forEach(t -> section.get(t.getValue()).addAll(entry.getValue().get(i.get())));
                } else {
                    first.forEach(t -> section.get(t.getValue()).addAll(entry.getValue().get(i.get())));
                }
                i.incrementAndGet();
            }
        }

        return table;
    }
}
