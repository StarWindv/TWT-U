package twtu.foundation.network.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;

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
                // 使用 getOrCreate：联机/独立服务器下客户端本地没有任何数据条目，
                // 用 get() 会返回 null 导致同步包被丢弃，口渴条永远不渲染
                // （主机因客户端与服务端共享 JVM 不受影响）
                IThirst cap = PlayerThirstStorage.getOrCreate(player);
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




