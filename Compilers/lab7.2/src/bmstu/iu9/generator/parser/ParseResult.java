package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.syntaxtree.ISyntaxTree;

public class ParseResult {
    private final Status status;
    private final ISyntaxTree syntaxTree;
    private final String message;

    public ParseResult(Status status, ISyntaxTree syntaxTree, String message) {
        this.status = status;
        this.syntaxTree = syntaxTree;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public ISyntaxTree getSyntaxTree() {
        return syntaxTree;
    }

    public String getMessage() {
        return message;
    }
}
