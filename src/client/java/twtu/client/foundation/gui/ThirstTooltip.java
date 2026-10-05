package twtu.client.foundation.gui;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import twtu.api.ThirstHelper;

import java.util.List;

/**
 * Appends a thirst and quench readout to the tooltip of anything that restores thirst.
 *
 * <p>Modded food carries its hydration values in {@link ThirstHelper}'s tables rather than in an
 * item component, so without this a player cannot tell that melon juice is worth more than a bowl
 * of soup until they drink it and watch the bar jump. Two numbers, no icons: the AppleSkin-style
 * icons already read as "hunger" to most players, and the point here is to make a number that is
 * otherwise invisible legible.
 *
 * <p>Only thirst and quench are shown. Nutrition and saturation are already on the vanilla tooltip,
 * and repeating them would make this longer for no gain.
 */
public class ThirstTooltip
{
    /** Amber, matching the thirst bar so the two read as the same system. */
    private static final ChatFormatting THIRST_COLOUR = ChatFormatting.GOLD;

    /** Aqua, matching the quench bar drawn underneath the thirst bar. */
    private static final ChatFormatting QUENCH_COLOUR = ChatFormatting.AQUA;

    public static void init()
    {
        ItemTooltipCallback.EVENT.register(ThirstTooltip::append);
    }

    private static void append(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines)
    {
        // isFood and isDrink both consult the blacklist, so a blacklisted item is skipped here the
        // same way it is skipped when eaten.
        if (!ThirstHelper.isDrink(stack) && !ThirstHelper.isFood(stack))
        {
            return;
        }

        // One line each rather than both on one: the labels are wide in most languages, and
        // running them together made the line too wide to read at a glance.
        lines.add(Component.translatable("twt-u.tooltip.thirst")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" " + ThirstHelper.getThirst(stack)).withStyle(THIRST_COLOUR)));
        lines.add(Component.translatable("twt-u.tooltip.quenched")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" " + ThirstHelper.getQuenched(stack)).withStyle(QUENCH_COLOUR)));
    }
}