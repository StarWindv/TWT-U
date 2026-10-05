package twtu;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twtu.api.ThirstHelper;
import twtu.compat.food.ModFoodRegistry;
import twtu.content.purity.WaterPurity;
import twtu.content.registry.CommandInit;
import twtu.content.registry.ItemInit;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.*;
import twtu.foundation.network.ThirstModPacketHandler;
import twtu.foundation.util.TickHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class TWTU implements ModInitializer {
    public static final String MOD_ID = "twt-u";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("TWT-U initializing...");

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

        // Record the food compat providers. Resolving their items here would be too early: Fabric
        // gives no ordering guarantee between mods' initializers, so another mod's items are not
        // necessarily registered yet. See ModFoodRegistry#process.
        ModFoodRegistry.registerBuiltins();

        // Initialize water purity system
        WaterPurity.init();

        // Register events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ThirstHelper.init();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                twtu.foundation.common.capability.IThirst cap = PlayerThirstStorage.get(player);
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

        LOGGER.info("TWT-U initialized!");
    }
}



