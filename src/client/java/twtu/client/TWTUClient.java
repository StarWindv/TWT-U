package twtu.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import twtu.content.purity.WaterPurity;
import twtu.content.thirst.DrinkByHandClient;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.gui.ThirstBarRenderer;
import twtu.foundation.gui.appleskin.HUDOverlayHandler;
import twtu.foundation.gui.appleskin.TooltipOverlayHandler;
import twtu.foundation.network.ThirstModPacketHandler;
import twtu.foundation.network.message.PlayerThirstSyncMessageClient;
import net.fabricmc.loader.api.FabricLoader;

public class TWTUClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Initialize HUD rendering
        ThirstBarRenderer.init();

        // Initialize drink by hand
        DrinkByHandClient.init();

        // Initialize appleskin-style overlays (no dependency on appleskin)
        HUDOverlayHandler.init();
        TooltipOverlayHandler.init();

        // Register tooltip callback for water purity
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            WaterPurity.renderPurityTooltip(stack, lines);
        });

        // Register client-side packet receiver
        ClientPlayNetworking.registerGlobalReceiver(ThirstModPacketHandler.PLAYER_THIRST_SYNC, PlayerThirstSyncMessageClient::handle);

        // 客户端加入世界时立即创建本地数据条目（默认值），
        // 否则联机客户端要等第一次同步包才有数据，且不会有口渴条
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                PlayerThirstStorage.getOrCreate(client.player);
            }
        });
    }
}




