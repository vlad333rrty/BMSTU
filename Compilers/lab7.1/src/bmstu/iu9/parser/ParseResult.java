package bmstu.iu9.parser;

import bmstu.iu9.parser.syntax.tree.ISyntaxTree;

public class ParseResult {
    private final ParseStatus status;
    private final ISyntaxTree syntaxTree;
    private final String message;

    public ParseResult(ParseStatus status, ISyntaxTree syntaxTree, String message){
        this.status = status;
        this.syntaxTree = syntaxTree;
        this.message = message;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public ISyntaxTree getSyntaxTree() {
        return syntaxTree;
    }

    public String getMessage() {
        return message;
    }
}
