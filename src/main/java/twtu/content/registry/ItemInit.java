package twtu.content.registry;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import twtu.TWTU;
import twtu.content.purity.WaterPurity;
import twtu.foundation.common.item.DrinkableItem;

public class ItemInit
{
    // 26.2 起物品 id 必须在构造 Item 之前通过 Properties#setId 设置
    public static final ResourceKey<Item> CLAY_BOWL_KEY = itemKey("clay_bowl");
    public static final ResourceKey<Item> TERRACOTTA_BOWL_KEY = itemKey("terracotta_bowl");
    public static final ResourceKey<Item> TERRACOTTA_WATER_BOWL_KEY = itemKey("terracotta_water_bowl");

    public static final Item CLAY_BOWL = new Item(new Item.Properties().setId(CLAY_BOWL_KEY).stacksTo(64));
    public static final Item TERRACOTTA_BOWL = new Item(new Item.Properties().setId(TERRACOTTA_BOWL_KEY).stacksTo(64));
    public static final Item TERRACOTTA_WATER_BOWL = new DrinkableItem(new Item.Properties().setId(TERRACOTTA_WATER_BOWL_KEY).stacksTo(64)).setContainer(TERRACOTTA_BOWL);

    public static final ResourceKey<CreativeModeTab> THIRST_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "thirst")
    );

    private static ResourceKey<Item> itemKey(String path)
    {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TWTU.MOD_ID, path));
    }

    public static void init()
    {
        Registry.register(BuiltInRegistries.ITEM, CLAY_BOWL_KEY, CLAY_BOWL);
        Registry.register(BuiltInRegistries.ITEM, TERRACOTTA_BOWL_KEY, TERRACOTTA_BOWL);
        Registry.register(BuiltInRegistries.ITEM, TERRACOTTA_WATER_BOWL_KEY, TERRACOTTA_WATER_BOWL);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, THIRST_TAB_KEY, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.twt-u"))
                .icon(() -> new ItemStack(TERRACOTTA_WATER_BOWL))
                .build());

        CreativeModeTabEvents.modifyOutputEvent(THIRST_TAB_KEY).register(entries -> {
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 0));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 1));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 2));
            entries.accept(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 3));
            entries.accept(WaterPurity.addPurity(PotionContents.createItemStack(Items.POTION, Potions.WATER), 0));
            entries.accept(WaterPurity.addPurity(PotionContents.createItemStack(Items.POTION, Potions.WATER), 1));
            entries.accept(WaterPurity.addPurity(PotionContents.createItemStack(Items.POTION, Potions.WATER), 2));
            entries.accept(WaterPurity.addPurity(PotionContents.createItemStack(Items.POTION, Potions.WATER), 3));
            entries.accept(new ItemStack(CLAY_BOWL));
            entries.accept(new ItemStack(TERRACOTTA_BOWL));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 0));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 1));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 2));
            entries.accept(WaterPurity.addPurity(new ItemStack(TERRACOTTA_WATER_BOWL), 3));
        });
    }
}
