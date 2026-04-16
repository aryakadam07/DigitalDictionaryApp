package dictionary;

public class WordSuggestion implements Comparable<WordSuggestion> {
    String word;
    int distance;

    public WordSuggestion(String word, int distance) {
        this.word = word;
        this.distance = distance;
    }

    public int compareTo(WordSuggestion other) {
        return this.distance - other.distance;
    }
}