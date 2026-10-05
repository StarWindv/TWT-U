package twtu.compat.food;

import net.fabricmc.loader.api.FabricLoader;
import twtu.TWTU;
import twtu.api.ThirstHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry of {@link ModFoods} providers, resolved once the first world starts.
 *
 * <p>Split into register and process on purpose:
 *
 * <ul>
 *   <li>{@link #register} only records the provider. It is safe to call during mod init, and
 *       resolving items there would not work: Fabric gives no ordering guarantee between mods'
 *       initializers, so another mod's items are not necessarily in the registry yet.</li>
 *   <li>{@link #process} runs every provider whose mod is loaded. It is called from
 *       {@link ThirstHelper#init()}, which the server lifecycle fires once the item registry is
 *       frozen.</li>
 * </ul>
 *
 * <p>Processing happens once. A datapack reload does not rebuild the hydration table, so nothing
 * can be registered twice.
 */
public final class ModFoodRegistry
{
    private static final List<ModFoods> PROVIDERS = new CopyOnWriteArrayList<>();
    private static volatile boolean processed;

    private ModFoodRegistry()
    {
    }

    /**
     * Adds a provider. Call this during your own mod init; it does no registry lookups.
     */
    public static void register(ModFoods provider)
    {
        PROVIDERS.add(provider);
    }

    /** Registers TWT-U's own providers. */
    public static void registerBuiltins()
    {
        register(new FarmersDelightFoods());
        register(new RusticDelightFoods());
        register(new MoreDelightFoods());
        register(new UbesDelightFoods());
        register(new ExDeorumFoods());
    }

    /**
     * Resolves every registered provider whose mod is loaded and adds its food to the hydration
     * table.
     *
     * <p>Runs on every call rather than once. {@link ThirstHelper#initTables} clears the tables
     * before rebuilding them, and the client calls that independently of the server, so a one-shot
     * guard would leave the client with an empty table in singleplayer. Re-running is harmless:
     * each call builds a fresh {@link FoodCollector}, and {@link ThirstHelper#addFood} uses
     * {@code putIfAbsent}, so nothing is registered twice.
     *
     * @return the total number of items the applied providers added
     */
    public static int process()
    {
        synchronized (ModFoodRegistry.class)
        {
            var applied = new ArrayList<String>(PROVIDERS.size());
            int total = 0;

            for (var provider : PROVIDERS)
            {
                var modId = provider.modId();

                if (!FabricLoader.getInstance().isModLoaded(modId))
                {
                    continue;
                }

                try
                {
                    var collector = new FoodCollector(modId);
                    provider.register(collector);

                    // A provider that resolves to nothing usually means the mod renamed its items
                    // upstream, which is worth surfacing. Resolving to nothing is never a crash.
                    if (collector.size() > 0)
                    {
                        applied.add(modId + " (" + collector.size() + ")");
                    }
                    else
                    {
                        TWTU.LOGGER.warn("Food compat: {} is loaded but none of its food items resolved", modId);
                    }

                    total += collector.size();
                }
                catch (Exception e)
                {
                    // One mod that renamed half its items must not take the rest of the hydration
                    // table down with it, and must never stop a world from loading.
                    TWTU.LOGGER.warn("Food compat: {} failed to register its food ({}), skipping", modId, e.toString());
                }
            }

            processed = true;

            if (!applied.isEmpty())
            {
                TWTU.LOGGER.info("Food compat active for {}", applied);
            }

            return total;
        }
    }

    /** Whether {@link #process} has already run. */
    public static boolean isProcessed()
    {
        return processed;
    }

    /** How many providers are registered, loaded mods or not. */
    public static int size()
    {
        return PROVIDERS.size();
    }
}