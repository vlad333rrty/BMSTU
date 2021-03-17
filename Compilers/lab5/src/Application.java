import io.FileUtils;
import modules.AbstractScanner;
import scanner.Compiler;
import tokens.EndToken;
import tokens.IToken;


public final class Application {
    public void start(String fileName){
        String program= FileUtils.readFile(fileName);
        AbstractScanner scanner=new Compiler().getScanner(program);
        for (IToken token = scanner.getNextToken(); token!= EndToken.getInstance(); token=scanner.getNextToken()){
            System.out.println(token);
        }
    }
}
