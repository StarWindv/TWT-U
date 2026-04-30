package twtu.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import twtu.content.purity.WaterPurity;
import twtu.content.thirst.DrinkByHandClient;
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
    }
}




