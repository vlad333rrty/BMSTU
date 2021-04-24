package bmstu.iu9.generator.semanticanalyzer;

public class SemanticAnalysisException extends Exception {
    private final String message;

    public SemanticAnalysisException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
