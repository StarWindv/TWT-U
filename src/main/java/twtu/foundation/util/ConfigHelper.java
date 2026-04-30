package twtu.foundation.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.*;

public class ConfigHelper
{
    public static Map<Item, Number[]> getItemsWithValues(List<? extends List<?>> source)
    {
        Map<Item, Number[]> map = new HashMap<>();
        for (List<?> entry : source)
        {
            String itemID = (String) entry.get(0);

            if (itemID.startsWith("#"))
            {
                final String tagID = itemID.replace("#", "");
                ResourceLocation tagLoc = new ResourceLocation(tagID);
                TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), tagLoc);
                for (Item item : BuiltInRegistries.ITEM)
                {
                    if (item.builtInRegistryHolder().is(tagKey))
                    {
                        map.put(item, new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
                    }
                }
            }
            else
            {
                ResourceLocation loc = new ResourceLocation(itemID);
                Item newItem = BuiltInRegistries.ITEM.get(loc);

                if (newItem != null) map.put(newItem, new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
            }
        }
        return map;
    }

    public static List<Item> getItems(List<? extends String> source){
        List<Item> list = new ArrayList<>();
        for(String itemID : source){
            if (itemID.startsWith("#"))
            {
                final String tagID = itemID.replace("#", "");
                ResourceLocation tagLoc = new ResourceLocation(tagID);
                TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), tagLoc);
                for (Item item : BuiltInRegistries.ITEM)
                {
                    if (item.builtInRegistryHolder().is(tagKey))
                    {
                        list.add(item);
                    }
                }
            }
            else
            {
                ResourceLocation loc = new ResourceLocation(itemID);
                Item newItem = BuiltInRegistries.ITEM.get(loc);

                if (newItem != null) list.add(newItem);
            }
        }
        return list;
    }
}




