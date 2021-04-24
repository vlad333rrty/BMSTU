package bmstu.iu9.generator.modules;

import bmstu.iu9.generator.scanner.AbstractScanner;

public interface ICompiler {
    AbstractScanner getScanner(String program);
}
