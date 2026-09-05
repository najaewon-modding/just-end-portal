package net.njw.justendportal.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModPayloads {
    private ModPayloads() {}
    public static void register(RegisterPayloadHandlersEvent event) { event.registrar("1").playToClient(PendingStatePayload.TYPE, PendingStatePayload.STREAM_CODEC); }
}
