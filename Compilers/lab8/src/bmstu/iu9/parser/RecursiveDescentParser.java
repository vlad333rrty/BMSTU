package bmstu.iu9.parser;

import bmstu.iu9.data.Status;
import bmstu.iu9.grammar.Axiom;
import bmstu.iu9.grammar.GObject;
import bmstu.iu9.grammar.Grammar;
import bmstu.iu9.grammar.NonTerminal;
import bmstu.iu9.grammar.Terminal;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static bmstu.iu9.format.FormatUtils.format;
import static bmstu.iu9.utils.Utils.getTokenTag;
import static bmstu.iu9.utils.Utils.getTokenValue;

public class RecursiveDescentParser implements IParser {
    private static final String DECLARATION_START = "LEFT_SQUARE_BRACKET";
    private static final String NON_TERMINAL_TAG = "NON_TERMINAL";
    private static final String DELIMITER = "COLON";
    private static final String VALUE_TAG = "VAL";
    private static final String DECLARATION_END = "RIGHT_SQUARE_BRACKET";
    private static final String STAR = "STAR";

    private final List<IToken> program;

    private int index = 0;

    private final Set<NonTerminal> nonTerminals = new HashSet<>();
    private final Set<Terminal> terminals = new HashSet<>();
    private final Map<NonTerminal, List<List<GObject>>> rules = new HashMap<>();
    private Axiom axiom;

    private NonTerminal currentNonTerminal;

    public RecursiveDescentParser(List<IToken> program) {
        this.program = program;
    }

    @Override
    public ParseResult parse() {
        try {
            parseS();
        } catch (ParseException e) {
            return new ParseResult(Status.FAILURE, null, e.getMessage());
        }

        return new ParseResult(Status.SUCCESS, new Grammar(rules, nonTerminals, terminals, axiom), Messages.NO_WARNINGS);
    }

    /**
     * S -> [ E ] S?
     *
     * @throws ParseException
     */
    private void parseS() throws ParseException {
        IToken token = getNextToken();
        if (!getTokenTag(token).equals(DECLARATION_START)) {
            throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
        }
        parseE();
        token = getNextToken();
        if (!getTokenTag(token).equals(DECLARATION_END)){
            throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
        }
        if (getTokenValue(peekNextToken()).equals(EndToken.getValue())) {
            getNextToken();
        } else {
            parseS();
        }
    }

    /**
     * E -> nt : T
     * @throws ParseException
     */
    private void parseE() throws ParseException {
        IToken token = getNextToken();
        if (!getTokenTag(token).equals(NON_TERMINAL_TAG)) {
            throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
        }

        NonTerminal nonTerminal = new NonTerminal(getTokenValue(token));
        if (!nonTerminals.add(nonTerminal)){
            throw new ParseException(format(Messages.MULTIPLE_NON_TERMINAL__0__DECLARATIONS,getTokenValue(token)));
        }
        currentNonTerminal = nonTerminal;
        if (axiom == null) {
            axiom = new Axiom(getTokenValue(token));
        }

        token = getNextToken();
        if (!getTokenTag(token).equals(DELIMITER)) {
            throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
        }

        List<List<GObject>> rule = new ArrayList<>();
        rule.add(new ArrayList<>());
        rules.put(currentNonTerminal, rule);
        parseT(rule);
    }

    /**
     * T -> val F | nt F | [ T ] F
     * @param rule
     * @throws ParseException
     */
    private void parseT(List<List<GObject>> rule) throws ParseException {
        IToken token = peekNextToken();
        switch (getTokenTag(token)) {
            case VALUE_TAG:
                getNextToken();
                Terminal terminal = new Terminal(getTokenValue(token));
                terminals.add(terminal);
                rule.get(rule.size() - 1).add(terminal);
                parseF(rule);
                break;
            case NON_TERMINAL_TAG:
                getNextToken();
                NonTerminal nonTerminal = new NonTerminal(getTokenValue(token));
                rule.get(rule.size() - 1).add(nonTerminal);
                parseF(rule);
                break;
            case DECLARATION_START:
                getNextToken();
                List<List<GObject>> list1 = new ArrayList<>();
                list1.add(new ArrayList<>());
                parseT(list1);

                token = getNextToken();
                if (!getTokenTag(token).equals(DECLARATION_END)){
                    throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                }

                List<List<GObject>> list2 = new ArrayList<>();
                list2.add(new ArrayList<>());
                parseF(list2);

                List<List<GObject>> res = concat(list1, list2);
                res = concat(rule, res);
                rule.clear();
                rule.addAll(res);
                break;
            default:
                //throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                break;
        }
    }

    /**
     * F -> val F | nt F | [ T ] F | : T | *F
     * @param rule
     * @throws ParseException
     */
    private void parseF(List<List<GObject>> rule) throws ParseException {
        IToken token = peekNextToken();
        switch (getTokenTag(token)) {
            case VALUE_TAG:
                getNextToken();
                Terminal terminal = new Terminal(getTokenValue(token));
                terminals.add(terminal);
                rule.get(rule.size() - 1).add(terminal);
                parseF(rule);
                break;
            case NON_TERMINAL_TAG:
                getNextToken();
                NonTerminal nonTerminal = new NonTerminal(getTokenValue(token));
                rule.get(rule.size() - 1).add(nonTerminal);
                parseF(rule);
                break;
            case DECLARATION_START:
                getNextToken();
                List<List<GObject>> list1 = new ArrayList<>();
                list1.add(new ArrayList<>());
                parseT(list1);

                token=getNextToken();
                if (!getTokenTag(token).equals(DECLARATION_END)){
                    throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                }

                List<List<GObject>> list2 = new ArrayList<>();
                list2.add(new ArrayList<>());
                parseF(list2);

                List<List<GObject>> res = concat(list1, list2);
                res = concat(rule, res);
                rule.clear();
                rule.addAll(res);
                break;
            case STAR:
                getNextToken();
                rule.get(rule.size() - 1).add(new Terminal(STAR));
                parseF(rule);
                break;
            case DELIMITER:
                getNextToken();
                rule.add(new ArrayList<>());
                parseT(rule);
                break;
            default:
                //throw new ParseException(format(Messages.UNEXPECTED_TOKEN__0, getTokenValue(token)));
                break;
        }
    }

    private List<List<GObject>> concat(List<List<GObject>> list1, List<List<GObject>> list2) {
        List<List<GObject>> result = new ArrayList<>();
        boolean flag = false;
        for (List<GObject> l : list1) {
            for (List<GObject> k : list2) {
                List<GObject> current = new ArrayList<>(l);
                for (int i = 0; i < k.size(); i++) {
                    if (k.get(i).getValue().equals(STAR)) {
                        List<GObject> list = new ArrayList<>(l);
                        for (int j = i + 1; j < k.size(); j++) {
                            list.add(k.get(j));
                        }
                        result.add(list);
                        List<GObject> epsilonList = new ArrayList<>();
                        epsilonList.add(new Terminal("epsilon"));
                        for (int j = i + 1; j < k.size(); j++) {
                            epsilonList.add(k.get(j));
                        }
                        result.add(epsilonList);
                        flag = true;
                        break;
                    } else if (k.get(i).getValue().equals("epsilon")){
                        // do nothing
                    }else {
                        current.add(k.get(i));
                    }
                }
                if (!flag){
                    result.add(current);
                }
                flag = false;
            }
        }
        return result;
    }

    private IToken getNextToken() {
        if (index == program.size()) {
            return EndToken.getInstance();
        }
        return program.get(index++);
    }

    private IToken peekNextToken() {
        if (index == program.size()) {
            return EndToken.getInstance();
        }
        return program.get(index);
    }
}
