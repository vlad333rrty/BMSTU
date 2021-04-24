package bmstu.iu9.generator.modules;

import bmstu.iu9.generator.scanner.AbstractScanner;
import bmstu.iu9.generator.scanner.Scanner;

public class Compiler implements ICompiler {

    @Override
    public AbstractScanner getScanner(String program) {
        return new Scanner(program);
    }
}
