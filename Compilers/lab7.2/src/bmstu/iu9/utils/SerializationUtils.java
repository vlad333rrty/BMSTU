package bmstu.iu9.utils;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.io.FileUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class SerializationUtils {
    private static final String TABLE_SERIALIZATION_FILE_NAME = "serialized/serialized_table.txt";
    private static final String GRAMMAR_SERIALIZATION_FILE_NAME = "serialized/serialized_grammar.txt";

    public static void serialize(Map<NonTerminal, Map<String, List<GObject>>> table, String axiomName, String epsilonName) {
        try {
            FileOutputStream os = new FileOutputStream(TABLE_SERIALIZATION_FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(table);
            oos.close();

            os = new FileOutputStream(GRAMMAR_SERIALIZATION_FILE_NAME);
            oos = new ObjectOutputStream(os);
            oos.writeObject(axiomName);
            oos.writeObject(epsilonName);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<NonTerminal, Map<String, List<GObject>>> deserializeTable()
            throws IOException, ClassNotFoundException {
        FileInputStream fis;
        ObjectInputStream in;
        Map<NonTerminal, Map<String, List<GObject>>> table;
        fis = new FileInputStream(TABLE_SERIALIZATION_FILE_NAME);
        in = new ObjectInputStream(fis);
        table = (Map<NonTerminal, Map<String, List<GObject>>>) in.readObject();
        in.close();
        return table;
    }

    public static String deserializeAxiomName()
            throws IOException, ClassNotFoundException {
        FileInputStream fis;
        ObjectInputStream in;
        String axiomName;
        fis = new FileInputStream(GRAMMAR_SERIALIZATION_FILE_NAME);
        in = new ObjectInputStream(fis);
        axiomName = (String) in.readObject();
        in.close();
        return axiomName;
    }

    public static String deserializeEpsilonName() throws IOException, ClassNotFoundException {
        FileInputStream fis;
        ObjectInputStream in;
        String axiomName;
        fis = new FileInputStream(GRAMMAR_SERIALIZATION_FILE_NAME);
        in = new ObjectInputStream(fis);
        in.readObject(); // skip first record
        axiomName = (String) in.readObject();
        in.close();
        return axiomName;
    }

}
