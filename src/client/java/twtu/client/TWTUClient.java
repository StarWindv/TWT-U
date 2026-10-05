package twtu.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import twtu.api.ThirstHelper;
import twtu.client.foundation.gui.ThirstTooltip;
import twtu.content.purity.WaterPurity;
import twtu.content.thirst.DrinkByHandClient;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.gui.ThirstBarRenderer;
import twtu.foundation.gui.appleskin.HUDOverlayHandler;
import twtu.foundation.gui.appleskin.TooltipOverlayHandler;
import twtu.foundation.network.message.PlayerThirstSyncPayload;
import twtu.foundation.network.message.PlayerThirstSyncMessageClient;

public class TWTUClient implements ClientModInitializer {

    private static boolean tablesReady = false;

    @Override
    public void onInitializeClient() {
        // Initialize HUD rendering
        ThirstBarRenderer.init();

        // Initialize drink by hand
        DrinkByHandClient.init();

        // Initialize appleskin-style overlays (no dependency on appleskin)
        HUDOverlayHandler.init();
        TooltipOverlayHandler.init();

        // Show the thirst and quench each item is worth on hover
        ThirstTooltip.init();

        // 客户端要自己建一份含水量表，否则提示与手持预览都是空的。
        // 挂在第一次客户端 tick 上而不是 onInitializeClient：此时物品注册表已冻结，
        // 解析其他模组的物品才可靠（entrypoint 阶段不行，见 ItemInit 的 setId 备注）。
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && !tablesReady)
            {
                tablesReady = true;
                ThirstHelper.initTables();
            }
        });

        // Register tooltip callback for water purity
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            WaterPurity.renderPurityTooltip(stack, lines);
        });

        // Register client-side packet receiver
        ClientPlayNetworking.registerGlobalReceiver(PlayerThirstSyncPayload.TYPE, PlayerThirstSyncMessageClient::handle);

        // 客户端加入世界时立即创建本地数据条目（默认值），
        // 否则远程客户端要等第一次同步包才有数据，且不会有口渴条
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                PlayerThirstStorage.getOrCreate(client.player);
            }
        });
    }
}
