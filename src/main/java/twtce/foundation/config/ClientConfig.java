package twtce.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientConfig
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean ONLY_SHOW_PURITY_WHEN_SHIFTING = false;
    public static int THIRST_BAR_Y_OFFSET = 0;
    public static int THIRST_BAR_X_OFFSET = 0;
    public static boolean DRINK_BOTH_HAND_NEEDED = true;

    public static void setup()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("twt-ce");
        try { Files.createDirectories(configDir); } catch (Exception ignored) {}

        Path configFile = configDir.resolve("client.json");

        if (Files.exists(configFile))
        {
            try (Reader reader = Files.newBufferedReader(configFile))
            {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                applyConfig(data);
            }
            catch (Exception e)
            {
                applyConfig(new ConfigData());
            }
        }
        else
        {
            ConfigData data = new ConfigData();
            applyConfig(data);
            saveConfig(configFile, data);
        }
    }

    private static void applyConfig(ConfigData data)
    {
        ONLY_SHOW_PURITY_WHEN_SHIFTING = data.onlyShowPurityWhenShifting;
        THIRST_BAR_Y_OFFSET = data.thirstBarYOffset;
        THIRST_BAR_X_OFFSET = data.thirstBarXOffset;
        DRINK_BOTH_HAND_NEEDED = data.drinkBothHandNeeded;
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
        boolean onlyShowPurityWhenShifting = false;
        int thirstBarYOffset = 0;
        int thirstBarXOffset = 0;
        boolean drinkBothHandNeeded = true;
    }
}
