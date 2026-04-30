package twtu.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ContainerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<String> CONTAINERS = Arrays.asList(
            "collectorsreap:pomegranate_black_tea",
            "collectorsreap:lime_green_tea"
    );

    public static void setup()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("twt-u");
        try { Files.createDirectories(configDir); } catch (Exception ignored) {}

        Path configFile = configDir.resolve("container.json");

        if (Files.exists(configFile))
        {
            try (Reader reader = Files.newBufferedReader(configFile))
            {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                CONTAINERS = data.containers;
            }
            catch (Exception e)
            {
                // keep defaults
            }
        }
        else
        {
            ConfigData data = new ConfigData();
            saveConfig(configFile, data);
        }
    }

    private static void saveConfig(Path path, ConfigData data)
    {
        try (Writer writer = Files.newBufferedWriter(path))
        {
            GSON.toJson(data, writer);
        }
        catch (Exception ignored) {}
    }

    private static class ConfigData
    {
        List<String> containers = Arrays.asList(
                "collectorsreap:pomegranate_black_tea",
                "collectorsreap:lime_green_tea"
        );
    }
}




