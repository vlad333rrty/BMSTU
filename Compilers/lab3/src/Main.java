import java.io.*;

public class Main {
    private static final String TEST_FILE_NAME="input.txt";

    public static void main(String[] args) {
        String input = readFile(TEST_FILE_NAME);
        Lexer lexer = new Lexer(input);
        for (Token token = lexer.getNextToken(); token!= Token.getEndToken(); token=lexer.getNextToken()){
            System.out.println(token);
        }
    }

    private static String readFile(String fileName) {
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
