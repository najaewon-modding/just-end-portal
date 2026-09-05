package net.njw.justendportal.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PendingSyncEvents {
    private PendingSyncEvents() {}
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) PendingStateSync.send(player); }
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) { if (event.getEntity() instanceof ServerPlayer player) PendingStateSync.send(player); }
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { if (event.getEntity() instanceof ServerPlayer player) PendingStateSync.send(player); }
}
