package bmstu.iu9.parser;

import bmstu.iu9.data.Status;
import bmstu.iu9.grammar.IGrammar;

public class ParseResult {
    private final Status status;
    private final IGrammar constructedGrammar;
    private final String message;

    public ParseResult(Status status, IGrammar constructedGrammar, String message) {
        this.status = status;
        this.constructedGrammar = constructedGrammar;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public IGrammar getConstructedGrammar() {
        return constructedGrammar;
    }

    public String getMessage() {
        return message;
    }
}
