package mephi.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FileRepository<K, V> {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final Map<K, V> data = new ConcurrentHashMap<>();
    protected final String fileDb;
    private final Type mapType;

    protected FileRepository(String fileDb, TypeToken<ConcurrentHashMap<K, V>> typeToken) {
        this.fileDb = fileDb;
        this.mapType = typeToken.getType();
    }

    protected synchronized void saveToFile() {
        try (FileWriter writer = new FileWriter(fileDb)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных в файл " + fileDb + ": " + e.getMessage());
        }
    }

    protected void loadFromFile() {
        File file = new File(fileDb);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<K, V> loadedData = GSON.fromJson(reader, mapType);

            if (loadedData != null) {
                data.putAll(loadedData);
                afterLoad(loadedData);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении данных из файла " + fileDb + ": " + e.getMessage());
        }
    }

    protected void afterLoad(Map<K, V> loadedData) {
    }
}