package dictionary;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DictionaryService {

    private Trie trie = new Trie();
    private SpellChecker spellChecker = new SpellChecker();

    // HashMap → frequency count
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // TreeMap → word categories
    private TreeMap<String, List<String>> categoryTree = new TreeMap<>();

    // Store all words loaded from words.txt
    private List<String> words = new ArrayList<>();

    // Constructor
    public DictionaryService() {
        loadWordsFromFile();
        buildCategories();
    }

    // Load dataset into Trie
    private void loadWordsFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("words.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();

                if (!word.isEmpty()) {
                    words.add(word);
                    trie.insert(word);
                }
            }

            br.close();

            System.out.println("Words loaded into Trie: " + words.size());

        } catch (IOException e) {
            System.out.println("Error loading words.txt file.");
            e.printStackTrace();
        }
    }

    // Build sample Tree categories
    private void buildCategories() {

        categoryTree.put("noun", Arrays.asList(
                "apple", "banana", "computer", "teacher", "doctor", "car", "book"
        ));

        categoryTree.put("verb", Arrays.asList(
                "run", "walk", "jump", "write", "read", "sing"
        ));

        categoryTree.put("adjective", Arrays.asList(
                "happy", "sad", "beautiful", "strong", "bright"
        ));
    }

    // Search word in Trie
    public boolean searchWord(String word) {
        word = word.toLowerCase();

        frequencyMap.put(word,
                frequencyMap.getOrDefault(word, 0) + 1);

        return trie.search(word);
    }

    // Prefix Suggestions
    public List<String> prefixSuggestions(String prefix) {
        return trie.prefixSearch(prefix.toLowerCase());
    }

    // Auto-correct suggestions
    public List<String> autoCorrect(String word) {
        return spellChecker.suggest(word.toLowerCase(), words);
    }

    // Get API Meaning
    public String getMeaning(String word) {
        return ApiDictionary.getWordMeaning(word);
    }

    // Get frequency count map
    public HashMap<String, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    // Get Tree categories
    public TreeMap<String, List<String>> getCategoryTree() {
        return categoryTree;
    }
}