package twtce.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemSettingsConfig
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<List<?>> DRINKS = new ArrayList<>();
    public static List<List<?>> FOODS = new ArrayList<>();
    public static List<String> ITEMS_BLACKLIST = new ArrayList<>();

    static
    {
        DRINKS.add(Arrays.asList("minecraft:potion", 6, 8));
        DRINKS.add(Arrays.asList("twt-ce:terracotta_water_bowl", 4, 5));
        DRINKS.add(Arrays.asList("farmersdelight:apple_cider", 8, 13));
        DRINKS.add(Arrays.asList("farmersdelight:melon_juice", 8, 13));
        DRINKS.add(Arrays.asList("collectorsreap:pink_limeade", 8, 10));
        DRINKS.add(Arrays.asList("collectorsreap:berry_limeade", 8, 13));
        DRINKS.add(Arrays.asList("collectorsreap:limeade", 8, 13));
        DRINKS.add(Arrays.asList("collectorsreap:pink_limeade", 8, 13));
        DRINKS.add(Arrays.asList("collectorsreap:pomegranate_black_tea", 10, 14));
        DRINKS.add(Arrays.asList("collectorsreap:lime_green_tea", 10, 14));

        FOODS.add(Arrays.asList("minecraft:apple", 2, 3));
        FOODS.add(Arrays.asList("minecraft:golden_apple", 2, 3));
        FOODS.add(Arrays.asList("minecraft:enchanted_golden_apple", 2, 3));
        FOODS.add(Arrays.asList("minecraft:melon_slice", 4, 5));
        FOODS.add(Arrays.asList("minecraft:carrot", 1, 2));
        FOODS.add(Arrays.asList("minecraft:mushroom_stew", 2, 3));
        FOODS.add(Arrays.asList("minecraft:rabbit_stew", 2, 3));
        FOODS.add(Arrays.asList("minecraft:beetroot_soup", 5, 7));
        FOODS.add(Arrays.asList("minecraft:beetroot", 1, 2));
        FOODS.add(Arrays.asList("minecraft:sweet_berries", 1, 2));
        FOODS.add(Arrays.asList("minecraft:glow_berries", 1, 2));
        FOODS.add(Arrays.asList("minecraft:golden_carrot", 1, 2));
        FOODS.add(Arrays.asList("farmersdelight:pumpkin_slice", 2, 1));
        FOODS.add(Arrays.asList("farmersdelight:cabbage_leaf", 1, 2));
        FOODS.add(Arrays.asList("farmersdelight:melon_popsicle", 7, 9));
        FOODS.add(Arrays.asList("farmersdelight:fruit_salad", 6, 8));
        FOODS.add(Arrays.asList("farmersdelight:tomato_sauce", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:mixed_salad", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:beef_stew", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:chicken_soup", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:vegetable_soup", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:fish_stew", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:pumpkin_soup", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:baked_cod_stew", 4, 5));
        FOODS.add(Arrays.asList("farmersdelight:noodle_soup", 4, 5));
        FOODS.add(Arrays.asList("collectorsreap:lime_slice", 1, 2));
        FOODS.add(Arrays.asList("collectorsreap:lime", 2, 3));
        FOODS.add(Arrays.asList("collectorsreap:portobello_rice_soup", 6, 8));
        FOODS.add(Arrays.asList("collectorsreap:lime_popsicle", 7, 9));

        ITEMS_BLACKLIST.add("examplemod:example_item_1");
        ITEMS_BLACKLIST.add("examplemod:example_item_2");
    }

    public static void setup()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("twt-ce");
        try { Files.createDirectories(configDir); } catch (Exception ignored) {}

        Path configFile = configDir.resolve("item_settings.json");

        if (Files.exists(configFile))
        {
            try (Reader reader = Files.newBufferedReader(configFile))
            {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                DRINKS = data.drinks;
                FOODS = data.foods;
                ITEMS_BLACKLIST = data.itemsBlacklist;
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
        List<List<?>> drinks = DRINKS;
        List<List<?>> foods = FOODS;
        List<String> itemsBlacklist = ITEMS_BLACKLIST;
    }
}
