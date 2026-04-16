package dictionary;

public class Word {
    private String word;
    private String category;

    public Word(String word, String category) {
        this.word = word;
        this.category = category;
    }

    public String getWord() {
        return word;
    }

    public String getCategory() {
        return category;
    }
}