package net.njw.justendportal.network;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.njw.justendportal.data.PendingPortalSavedData;

public final class PendingStateSync {
    private PendingStateSync() {}
    public static void send(ServerPlayer player) { send(player, PendingPortalSavedData.get(player.level().getServer()).getEntry(player.getUUID()).isPresent()); }
    public static void send(ServerPlayer player, boolean pending) { PacketDistributor.sendToPlayer(player, new PendingStatePayload(pending)); }
    public static void sendToOwner(MinecraftServer server, UUID ownerId, boolean pending) { ServerPlayer player = server.getPlayerList().getPlayer(ownerId); if (player != null) send(player, pending); }
}
