package twtce.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class KeyWordConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean ENABLE_KEYWORD_CONFIG = false;

    public static int DEFAULT_DRINK_HYDRATION = 10;
    public static int DEFAULT_DRINK_QUENCHNESS = 14;
    public static int DEFAULT_SOUP_HYDRATION = 4;
    public static int DEFAULT_SOUP_QUENCHNESS = 5;
    public static int DEFAULT_FRUIT_HYDRATION = 2;
    public static int DEFAULT_FRUIT_QUENCHNESS = 3;
    public static String KEYWORD_BLACKLIST = "(?:\\b|[^a-zA-Z])(dried|candied|leaf|leaves|gummy|crate|jam|sauce|bucket|seed|cookie|pie|bush|sapling|bean|curry|cake|candy)(?:\\b|[^a-zA-Z])";
    public static String KEYWORD_DRINK = "(?:\\b|[^a-zA-Z])(drink|juice|tea|soda|coffee|wine|beer|cider|yogurt|milkshake|smoothie)(?:\\b|[^a-zA-Z])";
    public static String KEYWORD_SOUP = "(?:\\b|[^a-zA-Z])(soup|stew|porridge)(?:\\b|[^a-zA-Z])";
    public static String KEYWORD_FRUIT = "(?:\\b|[^a-zA-Z])(fruit|berry|berries|grape|orange|peach|pear|coconut|lemon|melon|cherry|apple)(?:\\b|[^a-zA-Z])";

    public static void setup()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("twt-ce");
        try { Files.createDirectories(configDir); } catch (Exception ignored) {}

        Path configFile = configDir.resolve("keyword.json");

        if (Files.exists(configFile))
        {
            try (Reader reader = Files.newBufferedReader(configFile))
            {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                ENABLE_KEYWORD_CONFIG = data.enableKeywordConfig;
                DEFAULT_DRINK_HYDRATION = data.defaultDrinkHydration;
                DEFAULT_DRINK_QUENCHNESS = data.defaultDrinkQuenchness;
                DEFAULT_SOUP_HYDRATION = data.defaultSoupHydration;
                DEFAULT_SOUP_QUENCHNESS = data.defaultSoupQuenchness;
                DEFAULT_FRUIT_HYDRATION = data.defaultFruitHydration;
                DEFAULT_FRUIT_QUENCHNESS = data.defaultFruitQuenchness;
                KEYWORD_BLACKLIST = data.keywordBlacklist;
                KEYWORD_DRINK = data.keywordDrink;
                KEYWORD_SOUP = data.keywordSoup;
                KEYWORD_FRUIT = data.keywordFruit;
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

    public static int getDrinkHydration()
    {
        return DEFAULT_DRINK_HYDRATION;
    }

    public static int getDrinkQuenchness()
    {
        return DEFAULT_DRINK_QUENCHNESS;
    }

    public static int getSoupHydration()
    {
        return DEFAULT_SOUP_HYDRATION;
    }

    public static int getSoupQuenchness()
    {
        return DEFAULT_SOUP_QUENCHNESS;
    }

    public static int getFruitHydration()
    {
        return DEFAULT_FRUIT_HYDRATION;
    }

    public static int getFruitQuenchness()
    {
        return DEFAULT_FRUIT_QUENCHNESS;
    }

    private static class ConfigData
    {
        boolean enableKeywordConfig = false;
        int defaultDrinkHydration = 10;
        int defaultDrinkQuenchness = 14;
        int defaultSoupHydration = 4;
        int defaultSoupQuenchness = 5;
        int defaultFruitHydration = 2;
        int defaultFruitQuenchness = 3;
        String keywordBlacklist = "(?:\\b|[^a-zA-Z])(dried|candied|leaf|leaves|gummy|crate|jam|sauce|bucket|seed|cookie|pie|bush|sapling|bean|curry|cake|candy)(?:\\b|[^a-zA-Z])";
        String keywordDrink = "(?:\\b|[^a-zA-Z])(drink|juice|tea|soda|coffee|wine|beer|cider|yogurt|milkshake|smoothie)(?:\\b|[^a-zA-Z])";
        String keywordSoup = "(?:\\b|[^a-zA-Z])(soup|stew|porridge)(?:\\b|[^a-zA-Z])";
        String keywordFruit = "(?:\\b|[^a-zA-Z])(fruit|berry|berries|grape|orange|peach|pear|coconut|lemon|melon|cherry|apple)(?:\\b|[^a-zA-Z])";
    }
}
