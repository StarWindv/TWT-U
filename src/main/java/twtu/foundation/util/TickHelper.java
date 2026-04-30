package twtu.foundation.util;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

public class TickHelper
{
    /**
     * Util for running actions on the server delayed by n ticks
     * */
    private static final Map<Integer, List<Runnable>> tickTasks = new HashMap<>();
    private static int tickTimerFsr = 0;

    public static void init()
    {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTickCount();
            if (tickTimerFsr == 0 && tickTasks.containsKey(currentTick))
            {
                tickTasks.get(currentTick).forEach(Runnable::run);
                tickTasks.remove(currentTick);
                tickTimerFsr += 3;
            }
            else if (tickTimerFsr > 0)
                tickTimerFsr--;
        });
    }

    public static void addTask(int tick, Runnable task)
    {
        if(!tickTasks.containsKey(tick))
            tickTasks.put(tick, new ArrayList<>());

        tickTasks.get(tick).add(task);
    }

    public static void nextTick(Level level, Runnable task)
    {
        addTask(Objects.requireNonNull(level.getServer()).getTickCount() + 1, task);
    }

    public static void TickLater(Level level, int tickNumber, Runnable task)
    {
        addTask(Objects.requireNonNull(level.getServer()).getTickCount() + tickNumber, task);
    }
}




