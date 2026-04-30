package twtce.foundation.network.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import twtce.content.purity.WaterPurity;
import twtce.foundation.common.capability.IThirst;
import twtce.foundation.common.capability.PlayerThirstStorage;
import twtce.foundation.config.CommonConfig;

public class DrinkByHandMessage
{
    public BlockPos pos;

    public DrinkByHandMessage(BlockPos pos)
    {
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
    }

    public static DrinkByHandMessage decode(FriendlyByteBuf buffer)
    {
        return new DrinkByHandMessage(buffer.readBlockPos());
    }

    public static void handleOnServer(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender)
    {
        DrinkByHandMessage message = decode(buf);
        server.execute(() ->
        {
            Level level = player.level();
            IThirst cap = PlayerThirstStorage.get(player);
            if (cap == null) return;

            if(cap.getThirst() == 20)
                return;

            int purity = WaterPurity.getBlockPurity(level, message.pos);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);

            if(WaterPurity.givePurityEffects(player, purity))
                cap.drink(player, (int)CommonConfig.HAND_DRINKING_HYDRATION, (int)CommonConfig.HAND_DRINKING_QUENCHED);
        });
    }
}
