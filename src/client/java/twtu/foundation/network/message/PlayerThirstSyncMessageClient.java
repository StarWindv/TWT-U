package twtu.foundation.network.message;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;

public class PlayerThirstSyncMessageClient
{
    public static void handle(PlayerThirstSyncPayload payload, ClientPlayNetworking.Context context)
    {
        Minecraft client = context.client();
        client.execute(() ->
        {
            Player player = client.player;
            if (player != null)
            {
                // 使用 getOrCreate：远程客户端（联机/独立服务器）在收到同步包之前
                // 本地没有任何数据条目，用 get() 会返回 null 导致同步包被丢弃，
                // 口渴条永远不渲染（主机因共享 JVM 不受影响）。
                IThirst cap = PlayerThirstStorage.getOrCreate(player);
                if (cap != null)
                {
                    cap.setThirst(payload.thirst());
                    cap.setQuenched(payload.quenched());
                    cap.setExhaustion(payload.exhaustion());
                    cap.setShouldTickThirst(payload.enable());
                }
            }
        });
    }
}
