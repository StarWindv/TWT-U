package twtce;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twtce.api.ThirstHelper;
import twtce.content.purity.WaterPurity;
import twtce.content.registry.CommandInit;
import twtce.content.registry.ItemInit;
import twtce.foundation.common.capability.PlayerThirstStorage;
import twtce.foundation.config.*;
import twtce.foundation.network.ThirstModPacketHandler;
import twtce.foundation.util.TickHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class TWTCE implements ModInitializer {
    public static final String MOD_ID = "twt-ce";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("TWT-CE initializing...");

        // Initialize configs
        ItemSettingsConfig.setup();
        CommonConfig.setup();
        ClientConfig.setup();
        KeyWordConfig.setup();
        ContainerConfig.setup();

        // Initialize tick helper
        TickHelper.init();

        // Initialize items and commands
        ItemInit.init();
        CommandInit.init();

        // Initialize water purity system
        WaterPurity.init();

        // Register events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ThirstHelper.init();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                twtce.foundation.common.capability.IThirst cap = PlayerThirstStorage.get(player);
                if (cap != null) {
                    cap.tick(player);
                }
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            PlayerThirstStorage.getOrCreate(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            CompoundTag tag = PlayerThirstStorage.save(player);
        });

        // Initialize networking
        ThirstModPacketHandler.init();

        LOGGER.info("TWT-CE initialized!");
    }
}
