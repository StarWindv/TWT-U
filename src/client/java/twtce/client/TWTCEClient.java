package twtce.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import twtce.content.purity.WaterPurity;
import twtce.content.thirst.DrinkByHandClient;
import twtce.foundation.gui.ThirstBarRenderer;
import twtce.foundation.gui.appleskin.HUDOverlayHandler;
import twtce.foundation.gui.appleskin.TooltipOverlayHandler;
import twtce.foundation.network.ThirstModPacketHandler;
import twtce.foundation.network.message.PlayerThirstSyncMessageClient;
import net.fabricmc.loader.api.FabricLoader;

public class TWTCEClient implements ClientModInitializer {

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
