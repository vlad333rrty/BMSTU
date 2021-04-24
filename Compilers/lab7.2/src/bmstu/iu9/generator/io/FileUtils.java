package bmstu.iu9.generator.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

    public static void writeToFile(String fileName,String value){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
