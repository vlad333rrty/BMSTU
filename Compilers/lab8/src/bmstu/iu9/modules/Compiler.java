package bmstu.iu9.modules;

import bmstu.iu9.scanner.AbstractScanner;
import bmstu.iu9.scanner.Scanner;

public class Compiler implements ICompiler {

    @Override
    public AbstractScanner getScanner(String program) {
        return new Scanner(program);
    }
}
