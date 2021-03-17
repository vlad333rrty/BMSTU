package modules;

public interface ICompiler {
    AbstractScanner getScanner(String program);
}
