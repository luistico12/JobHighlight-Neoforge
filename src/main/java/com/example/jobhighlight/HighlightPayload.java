package com.example.jobhighlight;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server -> Client: diese Bloecke sollen markiert werden.
 *
 * @param positions  zu markierende Blockpositionen
 * @param color      RGB-Farbe (0xRRGGBB)
 * @param durationMs Anzeigedauer in Millisekunden
 */
public record HighlightPayload(List<BlockPos> positions, int color, int durationMs) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HighlightPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JobHighlight.MODID, "highlight"));

    public static final StreamCodec<ByteBuf, HighlightPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), HighlightPayload::positions,
            ByteBufCodecs.VAR_INT, HighlightPayload::color,
            ByteBufCodecs.VAR_INT, HighlightPayload::durationMs,
            HighlightPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
