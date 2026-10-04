package twtu.foundation.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import twtu.TWTU;

public record PlayerThirstSyncPayload(int thirst, int quenched, float exhaustion, boolean enable) implements CustomPacketPayload
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "player_thirst_sync");
    public static final CustomPacketPayload.Type<PlayerThirstSyncPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerThirstSyncPayload> CODEC = CustomPacketPayload.codec(
            PlayerThirstSyncPayload::write,
            PlayerThirstSyncPayload::read
    );

    public PlayerThirstSyncPayload(boolean enable)
    {
        this(0, 0, 0.0F, enable);
    }

    private static PlayerThirstSyncPayload read(FriendlyByteBuf buffer)
    {
        return new PlayerThirstSyncPayload(buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readBoolean());
    }

    private void write(FriendlyByteBuf buffer)
    {
        buffer.writeInt(thirst);
        buffer.writeInt(quenched);
        buffer.writeFloat(exhaustion);
        buffer.writeBoolean(enable);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
