package bmstu.iu9.generator.table;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.io.FileUtils;

import java.util.List;
import java.util.Map;

public class TableSerializer {
    private static final String FILE_NAME = "serialized/table.txt";
    private final Map<NonTerminal, Map<String, List<GObject>>> table;

    public TableSerializer(Map<NonTerminal, Map<String, List<GObject>>> table){
        this.table = table;
    }

    public void serialize(){
        StringBuilder builder = new StringBuilder().append("int[][][] table = new int[][][]{\n");
        for (var entry:table.entrySet()){
            builder.append("{");
            for (var sl : entry.getValue().entrySet()){
                builder.append("{");
                for (GObject g:sl.getValue()){
                    builder.append(TableConstants.getConstant(g.getValue())).append(", ");
                }
                builder.append("},");
            }
            builder.append("},\n");
        }
        builder.append("};\n");
        FileUtils.writeToFile(FILE_NAME,builder.toString());
    }
}
