package scanner;

import data.INameDictionary;

import java.util.ArrayList;
import java.util.List;

public class NameDictionary implements INameDictionary {
    private final List<String> names=new ArrayList<>();

    @Override
    public void addName(String name) {
        names.add(name);
    }

    @Override
    public boolean contains(String name) {
        return names.contains(name);
    }

    @Override
    public String getName(int ind) {
        return names.get(ind);
    }
}
