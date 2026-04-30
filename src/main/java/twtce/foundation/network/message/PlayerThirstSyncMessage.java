package twtce.foundation.network.message;

import net.minecraft.network.FriendlyByteBuf;

public class PlayerThirstSyncMessage
{
    public int thirst;
    public int quenched;
    public float exhaustion;
    public boolean enable;

    public PlayerThirstSyncMessage(int thirst, int quenched, float exhaustion, boolean enable)
    {
        this.thirst = thirst;
        this.quenched = quenched;
        this.exhaustion = exhaustion;
        this.enable = enable;
    }

    public PlayerThirstSyncMessage(boolean enable)
    {
        this.enable = enable;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(thirst);
        buffer.writeInt(quenched);
        buffer.writeFloat(exhaustion);
        buffer.writeBoolean(enable);
    }

    public static PlayerThirstSyncMessage decode(FriendlyByteBuf buffer)
    {
        return new PlayerThirstSyncMessage(buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readBoolean());
    }
}
