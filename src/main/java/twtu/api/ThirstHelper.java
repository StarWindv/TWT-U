package twtu.api;

import twtu.content.purity.ContainerWithPurity;
import twtu.content.purity.WaterPurity;
import twtu.foundation.common.event.ThirstEventFactory;
import twtu.foundation.config.CommonConfig;
import twtu.foundation.config.ContainerConfig;
import twtu.foundation.config.ItemSettingsConfig;
import twtu.foundation.config.KeyWordConfig;
import twtu.foundation.util.ConfigHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static twtu.content.purity.WaterPurity.hasPurity;

public class ThirstHelper
{
    private static final float MODIFIER_HARSHNESS = 0.5f;
    public static Map<Item, Number[]> VALID_DRINKS = new HashMap<>();
    public static Map<Item, Number[]> VALID_FOODS = new HashMap<>();
    public static List<Item> containers = new ArrayList<>();

    public static void init(){
        VALID_DRINKS.clear();
        VALID_FOODS.clear();
        containers.clear();

        VALID_DRINKS.putAll(ConfigHelper.getItemsWithValues(ItemSettingsConfig.DRINKS));
        VALID_FOODS.putAll(ConfigHelper.getItemsWithValues(ItemSettingsConfig.FOODS));
        containers.addAll(ConfigHelper.getItems(ContainerConfig.CONTAINERS));

        ThirstEventFactory.onRegisterThirstValue();
        for (Item item : containers){
            if(item.equals(Items.AIR))
                continue;
            WaterPurity.addContainer(new ContainerWithPurity(new ItemStack(item)));
        }

        VALID_DRINKS.forEach((item, numbers) -> {
            if (item.getFoodProperties() != null) {
                if (!CommonConfig.ENABLE_DRINKS_NUTRITION){
                    try {
                        var field = item.getFoodProperties().getClass().getDeclaredField("nutrition");
                        field.setAccessible(true);
                        field.setInt(item.getFoodProperties(), 0);
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    public static String keywordBlackList = KeyWordConfig.KEYWORD_BLACKLIST;
    public static String keywordDrink = KeyWordConfig.KEYWORD_DRINK;
    public static String keywordSoup = KeyWordConfig.KEYWORD_SOUP;
    public static String keywordFruit = KeyWordConfig.KEYWORD_FRUIT;

    public static boolean itemRestoresThirst(ItemStack itemStack)
    {
        return isDrink(itemStack) ||
                isFood(itemStack) || checkKeywords(itemStack);
    }

    public static boolean isDrink(ItemStack itemStack)
    {
        return !ItemSettingsConfig.ITEMS_BLACKLIST.contains(itemStack.getItem().toString()) &&
                VALID_DRINKS.containsKey(itemStack.getItem());
    }


    public static boolean isFood(ItemStack itemStack)
    {
        return !ItemSettingsConfig.ITEMS_BLACKLIST.contains(itemStack.getItem().toString()) &&
                VALID_FOODS.containsKey(itemStack.getItem());
    }

    public static void addFood(Item item, int thirst, int quenched) {}

    public static void addDrink(Item item, int thirst, int quenched) {}

    public static int getThirst(ItemStack itemStack)
    {
        Item item = itemStack.getItem();

        if(VALID_DRINKS.containsKey(item)) {
            return VALID_DRINKS.get(item)[0].intValue();
        }
        else
            return VALID_FOODS.get(item)[0].intValue();
    }

    public static int getQuenched(ItemStack itemStack)
    {
        Item item = itemStack.getItem();

        if(VALID_DRINKS.containsKey(item))
            return VALID_DRINKS.get(item)[1].intValue();
        else
            return VALID_FOODS.get(item)[1].intValue();
    }

    public static int getPurity(ItemStack item)
    {
        if(!hasPurity(item))
            return CommonConfig.DEFAULT_PURITY;
        else {
            assert item.getTag() != null;
            return item.getTag().getInt("Purity");
        }
    }

    public static float getExhaustionFireProtModifier(Player player)
    {
        final float perLevelMultiplier = 0.0625f;
        int totalLevels = EnchantmentHelper.getDamageProtection(player.getArmorSlots(), player.damageSources().onFire()) / 2;

        return 1.0f - ((totalLevels * perLevelMultiplier) * 0.75f);
    }

    public static float getExhaustionFireResistanceModifier(Player player){
        if(player.hasEffect(MobEffects.FIRE_RESISTANCE)){
            return (float) CommonConfig.FIRE_RESISTANCE_DEHYDRATION /100;
        }else return 1.0f;
    }

    /**
     * Calculates the thirst depletion speed modifier based on the player's
     * temperature and humidity.
     */
    public static float getExhaustionBiomeModifier(Player player)
    {
        BlockPos pos = player.getOnPos();
        Level level = player.level();

        if(level.dimensionType().ultraWarm())
            return (float) CommonConfig.NETHER_THIRST_DEPLETION_MODIFIER;
        else
        {
            Biome biome = level.getBiome(pos).value();

            float humidity = biome.climateSettings.downfall() + 0.6f;
            if(humidity <= 0.6)
                humidity += 0.5;

            float temp = biome.getBaseTemperature() + 0.2f;

            if(temp <= 0)
                temp = (float) Math.exp(temp);
            else if(temp > 1)
                temp /= 2;

            float thirstModifier = (float) CommonConfig.THIRST_DEPLETION_MODIFIER * (temp  / humidity);

            if(thirstModifier < 1)
            {
                float modifierOffset = 1 - thirstModifier;
                modifierOffset *= MODIFIER_HARSHNESS;
                thirstModifier = 1 - modifierOffset;
            }

            return thirstModifier;
        }
    }

    /**
     * Function from Thirst Was Remade, handles water items added from the
     * keyword config file
     */
    private static boolean checkKeywords(ItemStack itemStack)
    {
        if(!KeyWordConfig.ENABLE_KEYWORD_CONFIG)
            return false;

        if(!itemStack.isEdible())
            return false;

        String pattern = keywordBlackList;
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(itemStack.getDescriptionId());

        if(matcher.find())
            return false;

        pattern = keywordDrink;
        matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(itemStack.getDescriptionId());

        boolean hasWater=matcher.find();
        if(hasWater)
        {
            VALID_DRINKS.put(itemStack.getItem(), new Number[]{
                    KeyWordConfig.getDrinkHydration(),
                    KeyWordConfig.getDrinkQuenchness()
            });
            return true;
        }

        pattern = keywordSoup;
        matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(itemStack.getDescriptionId());

        hasWater=matcher.find();
        if(hasWater)
        {
            VALID_FOODS.put(itemStack.getItem(), new Number[]{
                    KeyWordConfig.getSoupHydration(),
                    KeyWordConfig.getSoupQuenchness()
            });
            return true;
        }

        pattern = keywordFruit;
        matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(itemStack.getDescriptionId());

        hasWater = matcher.find();
        if(hasWater)
            VALID_FOODS.put(itemStack.getItem(), new Number[]{
                    KeyWordConfig.getFruitHydration(),
                    KeyWordConfig.getFruitQuenchness()
            });

        return hasWater;
    }
}




