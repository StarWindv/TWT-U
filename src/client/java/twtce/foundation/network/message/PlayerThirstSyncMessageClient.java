package twtce.foundation.network.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import twtce.foundation.common.capability.IThirst;
import twtce.foundation.common.capability.PlayerThirstStorage;

public class PlayerThirstSyncMessageClient
{
    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender)
    {
        PlayerThirstSyncMessage message = PlayerThirstSyncMessage.decode(buf);
        client.execute(() ->
        {
            Player player = client.player;
            if (player != null)
            {
                IThirst cap = PlayerThirstStorage.get(player);
                if (cap != null)
                {
                    cap.setThirst(message.thirst);
                    cap.setQuenched(message.quenched);
                    cap.setExhaustion(message.exhaustion);
                    cap.setShouldTickThirst(message.enable);
                }
            }
        });
    }
}
