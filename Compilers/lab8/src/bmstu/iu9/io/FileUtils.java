package bmstu.iu9.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class FileUtils {
    public static String readFile(String fileName) {
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
