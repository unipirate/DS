//Author: FuQuan Gao
//StudentID: 1648979

import java.util.ArrayList;
import java.util.List;

public class DictionaryEntry {
    private String word;
    private List<String> definition = new ArrayList<String>();

    // getter 和 setter
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public List<String> getDefinition() {
        return definition;
    }
    public void setDefinition(List<String> definition) { this.definition = definition; }
    public synchronized void addDefinition(String definition) { this.definition.add(definition); }
    public synchronized void removeDefinition(String definition) { this.definition.remove(definition); }

    @Override
    public String toString() {
        return "DictionaryEntry{" +
                "word='" + word + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }
}