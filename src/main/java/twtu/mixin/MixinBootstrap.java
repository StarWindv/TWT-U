package twtu.mixin;

import twtu.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin({Bootstrap.class})
public class MixinBootstrap
{
    @Inject(
            method = {"bootStrap"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/cauldron/CauldronInteractions;bootStrap()V",
                    shift = At.Shift.AFTER
            )},
            remap = true)
    private static void modifyCauldronInteractions(CallbackInfo ci) {
        CauldronInteractions.WATER.put(Items.GLASS_BOTTLE, (blockState, level, pos, player, hand, itemStack) -> {
            if (!level.isClientSide()) {
                Item item = itemStack.getItem();
                ItemStack result = PotionContents.createItemStack(Items.POTION, Potions.WATER);
                WaterPurity.addPurity(result, pos, level);
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, result));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(blockState, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return InteractionResult.SUCCESS;
        });
        CauldronInteractions.WATER.put(Items.BUCKET, (blockState, level, pos, player, hand, item) ->
                CauldronInteractions.fillBucket(blockState,
                        level,
                        pos,
                        player,
                        hand,
                        item,
                        WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), pos, level),
                        (p_175660_) -> p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL)
        );
    }
}
