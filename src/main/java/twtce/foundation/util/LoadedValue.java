package twtce.foundation.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.util.function.Supplier;

public class LoadedValue<T>
{
    /**
     * This class was taken from Cold Sweat
     */

    T value;
    Supplier<T> valueCreator;

    public LoadedValue(Supplier<T> valueCreator)
    {
        this.valueCreator = valueCreator;
        this.value = valueCreator.get();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.value = valueCreator.get());
    }

    public static <V> LoadedValue<V> of(Supplier<V> valueCreator)
    {
        return new LoadedValue<>(valueCreator);
    }

    public T get()
    {
        return value;
    }
}
