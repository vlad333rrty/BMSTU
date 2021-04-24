package bmstu.iu9.modules;

import bmstu.iu9.scanner.AbstractScanner;

public interface ICompiler {
    AbstractScanner getScanner(String program);
}
