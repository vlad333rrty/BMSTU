package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.tokens.IToken;

import java.util.List;
import java.util.Map;

public interface IParser {
    ParseResult parse(Map<NonTerminal, Map<String, List<GObject>>> table, List<IToken> program, String axiomName, String epsilonName);
}
