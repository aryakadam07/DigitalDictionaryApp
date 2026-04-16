package dictionary;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// FileLoader class to load words from words.txt
public class FileLoader {

    public static List<String> loadWords(String filename) {
        List<String> words = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();

                if (!word.isEmpty()) {
                    words.add(word);
                }
            }

            br.close();

            System.out.println("Words loaded: " + words.size());

        } catch (IOException e) {
            System.out.println("Error loading file: " + filename);
            e.printStackTrace();
        }

        return words;
    }
}