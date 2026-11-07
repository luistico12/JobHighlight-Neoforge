package com.example.jobhighlight;

import java.util.List;
import net.minecraft.core.BlockPos;

/** Haelt die aktuell markierten Bloecke auf dem Client (ohne Client-only-Imports, damit es auf dem Server nie Probleme gibt). */
public final class ClientHighlights {
    private static volatile List<BlockPos> positions = List.of();
    private static volatile int color = 0xFFFFFF;
    private static volatile long expiresAtMillis = 0L;

    private ClientHighlights() {}

    public static void set(List<BlockPos> newPositions, int newColor, int durationMs) {
        positions = List.copyOf(newPositions);
        color = newColor;
        expiresAtMillis = System.currentTimeMillis() + durationMs;
    }

    /** Aktuelle Positionen, oder eine leere Liste, wenn die Anzeige abgelaufen ist. */
    public static List<BlockPos> active() {
        if (System.currentTimeMillis() > expiresAtMillis) {
            return List.of();
        }
        return positions;
    }

    public static int color() {
        return color;
    }
}
