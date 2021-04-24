package bmstu.iu9.grammar;

import bmstu.iu9.tokens.EndToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bmstu.iu9.io.FileUtils.readFile;

public class Grammar implements IGrammar{
    private static final String DEFAULT_AXIOM_NAME = "S";
    private static final String EPSILON = "epsilon";

    private final Map<NonTerminal, List<List<GObject>>> rules = new HashMap<>();
    private final Set<Terminal> terminals = new HashSet<>();
    private final Set<NonTerminal> nonTerminals = new HashSet<>();

    private final Map<String, GObject> stringToGObject = new HashMap<>();
    private Axiom axiom;

    public Grammar(String grammarFileName) {
        init(readFile(grammarFileName));
    }

    @Override
    public String getAxiomName() {
        return axiom.getValue();
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

    private void init(String grammar) {
        grammar = grammar.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("start=(?<axiom>[A-Z][A-Z0-9]*)");
        Matcher matcher = pattern.matcher(grammar);
        if (matcher.find()) {
            axiom = new Axiom(matcher.group("axiom"));
        } else {
            axiom = new Axiom(DEFAULT_AXIOM_NAME);
        }
        pattern = Pattern.compile("(?<left>[A-Z][A-Z0-9]*)=(?<right>.+)");
        matcher = pattern.matcher(grammar);
        Pattern right = Pattern.compile("(.+?)\\|");
        Matcher rightSideMatcher;
        NonTerminal nonTerminal;
        while (matcher.find()) {
            String nonTerminalName = matcher.group("left");
            if ((nonTerminal = (NonTerminal) stringToGObject.get(nonTerminalName)) == null) {
                nonTerminal = new NonTerminal(nonTerminalName);
                stringToGObject.put(nonTerminalName, nonTerminal);
                nonTerminals.add(nonTerminal);
            }
            rules.put(nonTerminal, new ArrayList<>());
            rightSideMatcher = right.matcher(matcher.group("right"));
            List<List<GObject>> rightSide = rules.get(nonTerminal);
            while (rightSideMatcher.find()) {
                rightSide.add(new ArrayList<>());
                String rule = rightSideMatcher.group(1);
                Pattern gObjects = Pattern.compile("(<(?<nonTerminal>\\w+)>)|(\\((?<terminal>[{\\p{L},_\":\\[\\]]+)\\))");
                Matcher gObjectMatcher = gObjects.matcher(rule);
                while (gObjectMatcher.find()) {
                    String nextTerminalName = gObjectMatcher.group("terminal");
                    String nextNonTerminalName = gObjectMatcher.group("nonTerminal");
                    if (nextNonTerminalName != null) {
                        NonTerminal nextNonTerminal = (NonTerminal) stringToGObject.get(nextTerminalName);
                        if (nextNonTerminal == null) {
                            nextNonTerminal = new NonTerminal(nextNonTerminalName);
                            stringToGObject.put(nextNonTerminalName, nextNonTerminal);
                            nonTerminals.add(nextNonTerminal);
                        }
                        rightSide.get(rightSide.size() - 1).add(nextNonTerminal);
                    } else if (nextTerminalName != null) {
                        Terminal nextTerminal = (Terminal) stringToGObject.get(nextTerminalName);
                        if (nextTerminal == null) {
                            nextTerminal = new Terminal(nextTerminalName);
                            stringToGObject.put(nextTerminalName, nextTerminal);
                            terminals.add(nextTerminal);
                        }
                        rightSide.get(rightSide.size() - 1).add(nextTerminal);
                    }
                }
            }
        }
        terminals.add(new Terminal(EndToken.getValue()));
    }
}
