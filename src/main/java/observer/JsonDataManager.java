package observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
/**
 * Gère la sérialisation/désérialisation des données au format JSON.
 */
public class JsonDataManager {
    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    private static final String DATA_FILE = "notification_data.json";
    /**
     * Sauvegarde les données dans un fichier JSON.
     * @param data Données à sauvegarder.
     */
    public static void saveData(Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde: " + e.getMessage());
        }
    }
    /**
     * Charge les données depuis un fichier JSON.
     * @return Map des données, ou une Map vide si erreur.
     */
    public static Map<String, Object> loadData() {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}