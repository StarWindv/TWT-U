package twtce.foundation.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerThirstStorage
{
    private static final Map<UUID, IThirst> PLAYER_DATA = new HashMap<>();

    public static IThirst get(Player player)
    {
        return PLAYER_DATA.get(player.getUUID());
    }

    public static IThirst getOrCreate(Player player)
    {
        return PLAYER_DATA.computeIfAbsent(player.getUUID(), k -> new twtce.content.thirst.PlayerThirst());
    }

    public static void remove(Player player)
    {
        PLAYER_DATA.remove(player.getUUID());
    }

    public static void copy(Player oldPlayer, Player newPlayer, boolean wasDeath)
    {
        IThirst oldData = PLAYER_DATA.get(oldPlayer.getUUID());
        if (oldData != null)
        {
            IThirst newData = getOrCreate(newPlayer);
            if (!wasDeath)
            {
                newData.copy(oldData);
            }
            else
            {
                newData.setShouldTickThirst(oldData.getShouldTickThirst());
            }
        }
    }

    public static CompoundTag save(Player player)
    {
        IThirst data = PLAYER_DATA.get(player.getUUID());
        if (data != null)
        {
            return data.serializeNBT();
        }
        return new CompoundTag();
    }

    public static void load(Player player, CompoundTag tag)
    {
        IThirst data = getOrCreate(player);
        data.deserializeNBT(tag);
    }
}
