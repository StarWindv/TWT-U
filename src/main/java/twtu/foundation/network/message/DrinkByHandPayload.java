package twtu.foundation.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import twtu.TWTU;

public record DrinkByHandPayload(BlockPos pos) implements CustomPacketPayload
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "drink_by_hand");
    public static final CustomPacketPayload.Type<DrinkByHandPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, DrinkByHandPayload> CODEC = CustomPacketPayload.codec(
            DrinkByHandPayload::write,
            DrinkByHandPayload::read
    );

    private static DrinkByHandPayload read(FriendlyByteBuf buffer)
    {
        return new DrinkByHandPayload(buffer.readBlockPos());
    }

    private static void write(DrinkByHandPayload payload, FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(payload.pos());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
