package twtce.content.thirst;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import twtce.foundation.config.ClientConfig;
import twtce.foundation.config.CommonConfig;
import twtce.foundation.network.ThirstModPacketHandler;
import twtce.foundation.util.MathHelper;
import io.netty.buffer.Unpooled;

public class DrinkByHandClient
{
    public static void init()
    {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
        {
            if (CommonConfig.CAN_DRINK_BY_HAND && level.isClientSide && hand == InteractionHand.MAIN_HAND)
            {
                drinkByHand(level, player);
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) ->
        {
            if (CommonConfig.CAN_DRINK_BY_HAND && level.isClientSide && hand == InteractionHand.MAIN_HAND)
            {
                drinkByHand(level, player);
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });
    }

    private static void drinkByHand(Level level, Player player)
    {
        BlockPos blockPos = MathHelper.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos();
        boolean HandAvailable;

        if (level.getFluidState(blockPos).is(FluidTags.WATER) && player.isCrouching() && !player.isInvulnerable()) {

            if(!ClientConfig.DRINK_BOTH_HAND_NEEDED){
                HandAvailable = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
            }else {
                HandAvailable = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
            }
            if(HandAvailable){
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeBlockPos(blockPos);
                ClientPlayNetworking.send(ThirstModPacketHandler.DRINK_BY_HAND, buf);
            }
        }
    }
}
