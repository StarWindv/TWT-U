package twtce.content.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import twtce.TWTCE;
import twtce.content.purity.WaterPurity;
import twtce.foundation.common.item.DrinkableItem;

public class ItemInit
{
    public static final Item CLAY_BOWL = new Item(new Item.Properties().stacksTo(64));
    public static final Item TERRACOTTA_BOWL = new Item(new Item.Properties().stacksTo(64));
    public static final Item TERRACOTTA_WATER_BOWL = new DrinkableItem().setContainer(TERRACOTTA_BOWL);

    public static final ResourceKey<CreativeModeTab> THIRST_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            new ResourceLocation(TWTCE.MOD_ID, "thirst")
    );

    public static void init()
    {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TWTCE.MOD_ID, "clay_bowl"), CLAY_BOWL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TWTCE.MOD_ID, "terracotta_bowl"), TERRACOTTA_BOWL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(TWTCE.MOD_ID, "terracotta_water_bowl"), TERRACOTTA_WATER_BOWL);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, THIRST_TAB_KEY, FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.twt-ce"))
                .icon(() -> new ItemStack(TERRACOTTA_WATER_BOWL))
                .build());

        ItemGroupEvents.modifyEntriesEvent(THIRST_TAB_KEY).register(entries -> {
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 0));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 1));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 2));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 3));
            entries.accept(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 0));
            entries.accept(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 1));
            entries.accept(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 2));
            entries.accept(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 3));
            entries.accept(new ItemStack(CLAY_BOWL));
            entries.accept(new ItemStack(TERRACOTTA_BOWL));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 0));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 1));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 2));
            entries.accept(new ItemStack(TERRACOTTA_WATER_BOWL));
        });
    }
}
