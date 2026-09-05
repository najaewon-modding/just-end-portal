package net.njw.justendportal.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.njw.justendportal.JustEndPortal;

public final class PendingPortalSavedData extends SavedData {
    public record Cell(int x, int z) {
        public static final Codec<Cell> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("x").forGetter(Cell::x), Codec.INT.fieldOf("z").forGetter(Cell::z)).apply(instance, Cell::new));
    }

    public record Entry(String linkId, String dimension, int x, int y, int z, boolean linked, int endX, int endY, int endZ, List<Cell> cells) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("link_id").forGetter(Entry::linkId), Codec.STRING.fieldOf("dimension").forGetter(Entry::dimension), Codec.INT.fieldOf("x").forGetter(Entry::x), Codec.INT.fieldOf("y").forGetter(Entry::y), Codec.INT.fieldOf("z").forGetter(Entry::z), Codec.BOOL.optionalFieldOf("linked", false).forGetter(Entry::linked), Codec.INT.optionalFieldOf("end_x", 0).forGetter(Entry::endX), Codec.INT.optionalFieldOf("end_y", 0).forGetter(Entry::endY), Codec.INT.optionalFieldOf("end_z", 0).forGetter(Entry::endZ), Cell.CODEC.listOf().optionalFieldOf("cells", List.of(new Cell(0, 0))).forGetter(Entry::cells)).apply(instance, Entry::new));
        public BlockPos sourcePos() { return new BlockPos(x, y, z); }
        public BlockPos targetPos() { return new BlockPos(endX, endY, endZ); }
        public BlockPos sourcePos(Cell cell) { return sourcePos().offset(cell.x(), 0, cell.z()); }
        public BlockPos targetPos(Cell cell) { return targetPos().offset(cell.x(), 0, cell.z()); }
        public String targetDimension() { return Level.END.identifier().toString().equals(dimension) ? Level.OVERWORLD.identifier().toString() : Level.END.identifier().toString(); }
    }

    public record OwnedEntry(UUID ownerId, Entry entry, Cell cell) {}

    private static final Codec<PendingPortalSavedData> CODEC = Codec.unboundedMap(Codec.STRING, Entry.CODEC).fieldOf("entries").xmap(PendingPortalSavedData::new, data -> data.entries).codec();
    public static final SavedDataType<PendingPortalSavedData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(JustEndPortal.MODID, "pending_portals"), PendingPortalSavedData::new, CODEC);
    private final Map<String, Entry> entries;

    public PendingPortalSavedData() { this.entries = new HashMap<>(); }
    private PendingPortalSavedData(Map<String, Entry> entries) { this.entries = new HashMap<>(entries); }
    private static String key(UUID ownerId, String dimension) { return ownerId + "|" + dimension; }
    private static boolean ownedKey(String key, UUID ownerId) { return key.equals(ownerId.toString()) || key.startsWith(ownerId + "|"); }
    public static PendingPortalSavedData get(MinecraftServer server) { return server.getDataStorage().computeIfAbsent(TYPE); }
    public Optional<Entry> getEntry(UUID ownerId, String dimension) { Entry direct = entries.get(key(ownerId, dimension)); if (direct != null) return Optional.of(direct); Entry legacy = entries.get(ownerId.toString()); return legacy != null && legacy.dimension().equals(dimension) ? Optional.of(legacy) : Optional.empty(); }
    public List<Entry> getEntries(UUID ownerId) { return entries.entrySet().stream().filter(entry -> ownedKey(entry.getKey(), ownerId)).map(Map.Entry::getValue).toList(); }
    public boolean hasEntry(UUID ownerId, String dimension) { return getEntry(ownerId, dimension).isPresent(); }
    public boolean matches(UUID ownerId, UUID linkId) { return getEntries(ownerId).stream().anyMatch(entry -> entry.linkId().equals(linkId.toString())); }
    public void put(UUID ownerId, UUID linkId, String dimension, BlockPos pos) { entries.put(key(ownerId, dimension), new Entry(linkId.toString(), dimension, pos.getX(), pos.getY(), pos.getZ(), false, 0, 0, 0, List.of(new Cell(0, 0)))); setDirty(); }
    public boolean link(UUID ownerId, UUID linkId, BlockPos targetPos) { var found = entries.entrySet().stream().filter(mapEntry -> ownedKey(mapEntry.getKey(), ownerId) && mapEntry.getValue().linkId().equals(linkId.toString())).findFirst(); if (found.isEmpty()) return false; Entry entry = found.get().getValue(); entries.put(found.get().getKey(), new Entry(entry.linkId(), entry.dimension(), entry.x(), entry.y(), entry.z(), true, targetPos.getX(), targetPos.getY(), targetPos.getZ(), entry.cells())); setDirty(); return true; }
    public boolean addCell(UUID ownerId, String dimension, Cell cell) { Entry entry = getEntry(ownerId, dimension).orElse(null); if (entry == null || !entry.linked() || entry.cells().contains(cell)) return false; List<Cell> cells = new ArrayList<>(entry.cells()); cells.add(cell); String storageKey = entries.entrySet().stream().filter(mapEntry -> ownedKey(mapEntry.getKey(), ownerId) && mapEntry.getValue().linkId().equals(entry.linkId())).map(Map.Entry::getKey).findFirst().orElse(key(ownerId, dimension)); entries.put(storageKey, new Entry(entry.linkId(), entry.dimension(), entry.x(), entry.y(), entry.z(), true, entry.endX(), entry.endY(), entry.endZ(), List.copyOf(cells))); setDirty(); return true; }
    public Optional<OwnedEntry> findSourceOwned(String dimension, BlockPos pos) { return entries.entrySet().stream().filter(mapEntry -> mapEntry.getValue().linked() && mapEntry.getValue().dimension().equals(dimension)).flatMap(mapEntry -> mapEntry.getValue().cells().stream().filter(cell -> mapEntry.getValue().sourcePos(cell).equals(pos)).map(cell -> new OwnedEntry(UUID.fromString(mapEntry.getKey().split("\\|", 2)[0]), mapEntry.getValue(), cell))).findFirst(); }
    public boolean clear(UUID ownerId, UUID linkId) { var found = entries.entrySet().stream().filter(mapEntry -> ownedKey(mapEntry.getKey(), ownerId) && mapEntry.getValue().linkId().equals(linkId.toString())).findFirst(); if (found.isEmpty()) return false; entries.remove(found.get().getKey()); setDirty(); return true; }
    public boolean clear(UUID ownerId, String dimension) { Entry entry = getEntry(ownerId, dimension).orElse(null); return entry != null && clear(ownerId, UUID.fromString(entry.linkId())); }
    public boolean clear(UUID ownerId) { boolean changed = entries.keySet().removeIf(key -> ownedKey(key, ownerId)); if (changed) setDirty(); return changed; }
}
