package net.njw.justendportal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.njw.justendportal.JustEndPortal;

public record PendingStatePayload(boolean overworld, boolean end) implements CustomPacketPayload {
    public static final Type<PendingStatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(JustEndPortal.MODID, "pending_state"));
    public static final StreamCodec<ByteBuf, PendingStatePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, PendingStatePayload::overworld, ByteBufCodecs.BOOL, PendingStatePayload::end, PendingStatePayload::new);
    @Override public Type<PendingStatePayload> type() { return TYPE; }
}
