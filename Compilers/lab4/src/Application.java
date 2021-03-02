import modules.AbstractScanner;
import scanner.Compiler;
import scanner.Scanner;
import tokens.AbstractToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class Application {
    public void start(String fileName){
        String program=readFile(fileName);
        AbstractScanner scanner=new Compiler().getScanner(program);
        for (AbstractToken token=scanner.getNextToken();token!=null;token=scanner.getNextToken()){
            System.out.println(token);
        }
        ((Scanner)scanner).printMessageList();
    }

    private String readFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        String line;
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
