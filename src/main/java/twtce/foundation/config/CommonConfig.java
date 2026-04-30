package twtce.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommonConfig
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigData CONFIG;

    public static double THIRST_DEPLETION_MODIFIER = 1.2;
    public static boolean THIRST_DEPLETION_IN_PEACEFUL = false;
    public static double NETHER_THIRST_DEPLETION_MODIFIER = 3.0;
    public static int FIRE_RESISTANCE_DEHYDRATION = 50;
    public static boolean DEPLETES_WHEN_NAUSEA = true;
    public static boolean MOVE_SLOW_WHEN_THIRSTY = true;
    public static boolean CAN_DRINK_RAIN_WATER = true;
    public static boolean ENABLE_DRINKS_NUTRITION = true;
    public static int WATER_BOTTLE_STACKSIZE = 64;
    public static boolean DEHYDRATION_HALTS_HEALTH_REGEN = true;
    public static boolean HEALTH_REGEN_DEHYDRATION_IS_BIOME_DEPENDENT = true;
    public static boolean HEALTH_REGEN_DEPLETES_HYDRATION = true;
    public static boolean CAN_DRINK_BY_HAND = false;
    public static double HAND_DRINKING_HYDRATION = 3;
    public static double HAND_DRINKING_QUENCHED = 2;
    public static boolean EXTRA_HYDRATION_CONVERT_TO_QUENCHED = true;

    public static double MOUNTAINS_Y = 100;
    public static double CAVES_Y = 48;
    public static double RUNNING_WATER_PURIFICATION_AMOUNT = 1;

    public static int DEFAULT_PURITY = 2;
    public static boolean QUENCH_THIRST_WHEN_DEBUFFED = true;
    public static double DIRTY_POISON_PERCENTAGE = 30;
    public static double DIRTY_NAUSEA_PERCENTAGE = 100;
    public static double SLIGHTLY_DIRTY_POISON_PERCENTAGE = 10;
    public static double SLIGHTLY_DIRTY_NAUSEA_PERCENTAGE = 50;
    public static double ACCEPTABLE_POISON_PERCENTAGE = 0;
    public static double ACCEPTABLE_NAUSEA_PERCENTAGE = 5;
    public static double PURIFIED_POISON_PERCENTAGE = 0;
    public static double PURIFIED_NAUSEA_PERCENTAGE = 0;
    public static double KETTLE_PURIFICATION_LEVELS = 2;

    public static double FERMENTATION_MOLDING_THRESHOLD = 3;
    public static double FERMENTATION_MOLDING_HARSHNESS = 2;

    public static void setup()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("twt-ce");
        try
        {
            Files.createDirectories(configDir);
        }
        catch (Exception ignored) {}

        Path configFile = configDir.resolve("common.json");

        if (Files.exists(configFile))
        {
            try (Reader reader = Files.newBufferedReader(configFile))
            {
                CONFIG = GSON.fromJson(reader, ConfigData.class);
                applyConfig();
            }
            catch (Exception e)
            {
                CONFIG = new ConfigData();
                applyConfig();
            }
        }
        else
        {
            CONFIG = new ConfigData();
            applyConfig();
            saveConfig(configFile);
        }
    }

    private static void applyConfig()
    {
        THIRST_DEPLETION_MODIFIER = CONFIG.thirstDepletionModifier;
        THIRST_DEPLETION_IN_PEACEFUL = CONFIG.thirstDepletionInPeaceful;
        NETHER_THIRST_DEPLETION_MODIFIER = CONFIG.netherThirstDepletionModifier;
        FIRE_RESISTANCE_DEHYDRATION = CONFIG.fireResistanceDehydration;
        DEPLETES_WHEN_NAUSEA = CONFIG.depletesWhenNausea;
        MOVE_SLOW_WHEN_THIRSTY = CONFIG.moveSlowWhenThirsty;
        CAN_DRINK_RAIN_WATER = CONFIG.canDrinkRainWater;
        ENABLE_DRINKS_NUTRITION = CONFIG.enableDrinksNutrition;
        WATER_BOTTLE_STACKSIZE = CONFIG.waterBottleStacksize;
        DEHYDRATION_HALTS_HEALTH_REGEN = CONFIG.dehydrationHaltsHealthRegen;
        HEALTH_REGEN_DEHYDRATION_IS_BIOME_DEPENDENT = CONFIG.healthRegenDehydrationIsBiomeDependent;
        HEALTH_REGEN_DEPLETES_HYDRATION = CONFIG.healthRegenDepletesHydration;
        CAN_DRINK_BY_HAND = CONFIG.canDrinkByHand;
        HAND_DRINKING_HYDRATION = CONFIG.handDrinkingHydration;
        HAND_DRINKING_QUENCHED = CONFIG.handDrinkingQuenched;
        EXTRA_HYDRATION_CONVERT_TO_QUENCHED = CONFIG.extraHydrationConvertToQuenched;
        MOUNTAINS_Y = CONFIG.mountainsY;
        CAVES_Y = CONFIG.cavesY;
        RUNNING_WATER_PURIFICATION_AMOUNT = CONFIG.runningWaterPurificationAmount;
        DEFAULT_PURITY = CONFIG.defaultPurity;
        QUENCH_THIRST_WHEN_DEBUFFED = CONFIG.quenchThirstWhenDebuffed;
        DIRTY_POISON_PERCENTAGE = CONFIG.dirtyPoisonPercentage;
        DIRTY_NAUSEA_PERCENTAGE = CONFIG.dirtyNauseaPercentage;
        SLIGHTLY_DIRTY_POISON_PERCENTAGE = CONFIG.slightlyDirtyPoisonPercentage;
        SLIGHTLY_DIRTY_NAUSEA_PERCENTAGE = CONFIG.slightlyDirtyNauseaPercentage;
        ACCEPTABLE_POISON_PERCENTAGE = CONFIG.acceptablePoisonPercentage;
        ACCEPTABLE_NAUSEA_PERCENTAGE = CONFIG.acceptableNauseaPercentage;
        PURIFIED_POISON_PERCENTAGE = CONFIG.purifiedPoisonPercentage;
        PURIFIED_NAUSEA_PERCENTAGE = CONFIG.purifiedNauseaPercentage;
        KETTLE_PURIFICATION_LEVELS = CONFIG.kettlePurificationLevels;
        FERMENTATION_MOLDING_THRESHOLD = CONFIG.fermentationMoldingThreshold;
        FERMENTATION_MOLDING_HARSHNESS = CONFIG.fermentationMoldingHarshness;
    }

    private static void saveConfig(Path path)
    {
        try (Writer writer = Files.newBufferedWriter(path))
        {
            GSON.toJson(new ConfigData(), writer);
        }
        catch (Exception ignored) {}
    }

    private static class ConfigData
    {
        double thirstDepletionModifier = 1.2;
        boolean thirstDepletionInPeaceful = false;
        double netherThirstDepletionModifier = 3.0;
        int fireResistanceDehydration = 50;
        boolean depletesWhenNausea = true;
        boolean moveSlowWhenThirsty = true;
        boolean canDrinkRainWater = true;
        boolean enableDrinksNutrition = true;
        int waterBottleStacksize = 64;
        boolean dehydrationHaltsHealthRegen = true;
        boolean healthRegenDehydrationIsBiomeDependent = true;
        boolean healthRegenDepletesHydration = true;
        boolean canDrinkByHand = false;
        double handDrinkingHydration = 3;
        double handDrinkingQuenched = 2;
        boolean extraHydrationConvertToQuenched = true;
        double mountainsY = 100;
        double cavesY = 48;
        double runningWaterPurificationAmount = 1;
        int defaultPurity = 2;
        boolean quenchThirstWhenDebuffed = true;
        double dirtyPoisonPercentage = 30;
        double dirtyNauseaPercentage = 100;
        double slightlyDirtyPoisonPercentage = 10;
        double slightlyDirtyNauseaPercentage = 50;
        double acceptablePoisonPercentage = 0;
        double acceptableNauseaPercentage = 5;
        double purifiedPoisonPercentage = 0;
        double purifiedNauseaPercentage = 0;
        double kettlePurificationLevels = 2;
        double fermentationMoldingThreshold = 3;
        double fermentationMoldingHarshness = 2;
    }
}
