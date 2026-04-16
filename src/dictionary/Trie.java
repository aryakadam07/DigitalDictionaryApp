package dictionary;

import java.util.ArrayList;
import java.util.List;

public class Trie {

    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;

        for (char c : word.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;

            int index = c - 'a';

            if (node.children[index] == null)
                node.children[index] = new TrieNode();

            node = node.children[index];
        }

        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = root;

        for (char c : word.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;

            int index = c - 'a';

            if (node.children[index] == null)
                return false;

            node = node.children[index];
        }

        return node.isEnd;
    }

    public List<String> prefixSearch(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;

        for (char c : prefix.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;

            int index = c - 'a';

            if (node.children[index] == null)
                return result;

            node = node.children[index];
        }

        collectWords(node, prefix, result);
        return result;
    }

    private void collectWords(TrieNode node, String word, List<String> result) {
        if (node.isEnd)
            result.add(word);

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                collectWords(node.children[i], word + (char)(i + 'a'), result);
            }
        }
    }
}