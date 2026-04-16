package dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiDictionary {

    public static String getWordMeaning(String word) {
        try {
            String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject obj = jsonArray.getJSONObject(0);

            String wordText = obj.getString("word");
            String phonetic = obj.optString("phonetic", "N/A");

            JSONArray meanings = obj.getJSONArray("meanings");
            JSONObject meaningObj = meanings.getJSONObject(0);

            String partOfSpeech = meaningObj.getString("partOfSpeech");

            JSONArray definitions = meaningObj.getJSONArray("definitions");
            JSONObject defObj = definitions.getJSONObject(0);

            String definition = defObj.getString("definition");

            return "Word: " + wordText +
                    "\nPhonetic: " + phonetic +
                    "\nType: " + partOfSpeech +
                    "\nMeaning: " + definition;

        } catch (Exception e) {
            return "Word not found.";
        }
    }
}