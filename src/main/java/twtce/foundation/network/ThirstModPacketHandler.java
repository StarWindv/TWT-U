package twtce.foundation.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import twtce.TWTCE;
import twtce.foundation.network.message.DrinkByHandMessage;
import twtce.foundation.network.message.PlayerThirstSyncMessage;

public class ThirstModPacketHandler
{
    public static final ResourceLocation PLAYER_THIRST_SYNC = new ResourceLocation(TWTCE.MOD_ID, "player_thirst_sync");
    public static final ResourceLocation DRINK_BY_HAND = new ResourceLocation(TWTCE.MOD_ID, "drink_by_hand");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(DRINK_BY_HAND, DrinkByHandMessage::handleOnServer);
    }

    public static void sendToClient(ServerPlayer player, PlayerThirstSyncMessage message)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        message.encode(buf);
        ServerPlayNetworking.send(player, PLAYER_THIRST_SYNC, buf);
    }
}
