package bmstu.iu9.generator.semanticanalyzer;

import bmstu.iu9.generator.data.Status;
import bmstu.iu9.generator.grammar.IGrammar;

public class SemanticAnalysisResult {
    private final Status status;
    private final IGrammar constructedGrammar;
    private final String message;

    public SemanticAnalysisResult(Status status, IGrammar constructedGrammar, String message) {
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
