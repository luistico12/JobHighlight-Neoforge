package com.example.jobhighlight;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Nimmt das Payload auf dem Client entgegen. Enthaelt bewusst keine Minecraft-Client-Klassen. */
public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}

    public static void handle(HighlightPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientHighlights.set(payload.positions(), payload.color(), payload.durationMs()));
    }
}
