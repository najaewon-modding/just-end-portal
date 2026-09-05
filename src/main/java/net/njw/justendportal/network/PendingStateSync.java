package net.njw.justendportal.network;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.njw.justendportal.data.PendingPortalSavedData;

public final class PendingStateSync {
    private PendingStateSync() {}
    public static void send(ServerPlayer player) {
        var saved = PendingPortalSavedData.get(player.level().getServer());
        PacketDistributor.sendToPlayer(player, new PendingStatePayload(saved.hasEntry(player.getUUID(), Level.OVERWORLD.identifier().toString()), saved.hasEntry(player.getUUID(), Level.END.identifier().toString())));
    }
    public static void sendToOwner(MinecraftServer server, UUID ownerId) { ServerPlayer player = server.getPlayerList().getPlayer(ownerId); if (player != null) send(player); }
}
