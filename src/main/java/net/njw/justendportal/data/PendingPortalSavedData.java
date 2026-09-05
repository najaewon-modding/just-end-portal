package net.njw.justendportal.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.njw.justendportal.JustEndPortal;

public final class PendingPortalSavedData extends SavedData {
    public record Entry(String linkId, String dimension, int x, int y, int z) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("link_id").forGetter(Entry::linkId), Codec.STRING.fieldOf("dimension").forGetter(Entry::dimension), Codec.INT.fieldOf("x").forGetter(Entry::x), Codec.INT.fieldOf("y").forGetter(Entry::y), Codec.INT.fieldOf("z").forGetter(Entry::z)).apply(instance, Entry::new));
        public BlockPos pos() { return new BlockPos(x, y, z); }
    }

    private static final Codec<PendingPortalSavedData> CODEC = Codec.unboundedMap(Codec.STRING, Entry.CODEC).fieldOf("entries").xmap(PendingPortalSavedData::new, data -> data.entries).codec();
    public static final SavedDataType<PendingPortalSavedData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(JustEndPortal.MODID, "pending_portals"), PendingPortalSavedData::new, CODEC);
    private final Map<String, Entry> entries;

    public PendingPortalSavedData() { this.entries = new HashMap<>(); }
    private PendingPortalSavedData(Map<String, Entry> entries) { this.entries = new HashMap<>(entries); }
    public static PendingPortalSavedData get(MinecraftServer server) { return server.getDataStorage().computeIfAbsent(TYPE); }
    public Optional<Entry> getEntry(UUID ownerId) { return Optional.ofNullable(entries.get(ownerId.toString())); }
    public boolean matches(UUID ownerId, UUID linkId) { return getEntry(ownerId).map(entry -> entry.linkId().equals(linkId.toString())).orElse(false); }
    public void put(UUID ownerId, UUID linkId, String dimension, BlockPos pos) { entries.put(ownerId.toString(), new Entry(linkId.toString(), dimension, pos.getX(), pos.getY(), pos.getZ())); setDirty(); }
    public boolean clear(UUID ownerId, UUID linkId) { Entry entry = entries.get(ownerId.toString()); if (entry == null || !entry.linkId().equals(linkId.toString())) return false; entries.remove(ownerId.toString()); setDirty(); return true; }
}
