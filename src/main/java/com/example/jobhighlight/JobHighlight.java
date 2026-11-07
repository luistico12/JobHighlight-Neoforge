package com.example.jobhighlight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Shift-Rechtsklick auf einen Villager markiert dessen Arbeitsblock.
 *
 * Die Mod laeuft auf Server und Client: Nur der Server kennt den Arbeitsblock eines Villagers
 * (liegt im Brain), also ermittelt er die Position und schickt sie per Payload an den Client,
 * der den Block durch Waende hindurch markiert. In Einzelspielerwelten ist das automatisch erledigt,
 * weil dort ein integrierter Server laeuft.
 */
@Mod(JobHighlight.MODID)
public class JobHighlight {
    public static final String MODID = "jobhighlight";

    public JobHighlight(IEventBus modEventBus) {
        modEventBus.addListener(JobHighlight::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // optional(): Client und Server koennen sich auch verbinden, wenn die Mod nur auf einer Seite liegt
        event.registrar("1")
                .optional()
                .playToClient(HighlightPayload.TYPE, HighlightPayload.STREAM_CODEC, ClientPayloadHandler::handle);
    }
}
