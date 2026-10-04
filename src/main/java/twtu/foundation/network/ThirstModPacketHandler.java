package twtu.foundation.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import twtu.content.purity.WaterPurity;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.CommonConfig;
import twtu.foundation.network.message.DrinkByHandPayload;
import twtu.foundation.network.message.PlayerThirstSyncPayload;

public class ThirstModPacketHandler
{
    public static void init()
    {
        PayloadTypeRegistry.clientboundPlay().register(PlayerThirstSyncPayload.TYPE, PlayerThirstSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(DrinkByHandPayload.TYPE, DrinkByHandPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DrinkByHandPayload.TYPE, (payload, context) -> {
            context.server().execute(() ->
            {
                ServerPlayer player = context.player();
                var level = player.level();
                IThirst cap = PlayerThirstStorage.get(player);
                if (cap == null) return;

                if(cap.getThirst() == 20)
                    return;

                int purity = WaterPurity.getBlockPurity(level, payload.pos());
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

                if(WaterPurity.givePurityEffects(player, purity))
                    cap.drink(player, (int)CommonConfig.HAND_DRINKING_HYDRATION, (int)CommonConfig.HAND_DRINKING_QUENCHED);
            });
        });
    }

    public static void sendToClient(ServerPlayer player, PlayerThirstSyncPayload message)
    {
        ServerPlayNetworking.send(player, message);
    }
}
