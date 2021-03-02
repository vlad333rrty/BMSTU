package scanner;

import modules.AbstractScanner;
import modules.ICompiler;

public class Compiler implements ICompiler {

    @Override
    public AbstractScanner getScanner(String program) {
        return new Scanner(program);
    }
}
