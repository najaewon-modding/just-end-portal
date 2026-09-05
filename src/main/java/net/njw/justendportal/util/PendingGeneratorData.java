package net.njw.justendportal.util;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.registry.ModItems;

public final class PendingGeneratorData {
    private static final String PENDING = "njw_just_end_portal_pending";
    private static final String LINK_ID = "njw_just_end_portal_link_id";
    private static final String OWNER_ID = "njw_just_end_portal_owner_id";
    private static final String DIMENSION = "njw_just_end_portal_dimension";
    private static final String X = "njw_just_end_portal_x";
    private static final String Y = "njw_just_end_portal_y";
    private static final String Z = "njw_just_end_portal_z";

    public record Data(UUID linkId, UUID ownerId, String dimension, BlockPos pos) {
    }

    private PendingGeneratorData() {
    }

    public static Optional<Data> get(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return Optional.empty();
        CompoundTag tag = data.copyTag();
        if (!tag.getBooleanOr(PENDING, false)) return Optional.empty();
        try {
            UUID linkId = UUID.fromString(tag.getStringOr(LINK_ID, ""));
            UUID ownerId = UUID.fromString(tag.getStringOr(OWNER_ID, ""));
            String dimension = tag.getStringOr(DIMENSION, "");
            BlockPos pos = new BlockPos(tag.getIntOr(X, 0), tag.getIntOr(Y, 0), tag.getIntOr(Z, 0));
            return Optional.of(new Data(linkId, ownerId, dimension, pos));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static void set(ItemStack stack, UUID linkId, UUID ownerId, String dimension, BlockPos pos) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putBoolean(PENDING, true);
            tag.putString(LINK_ID, linkId.toString());
            tag.putString(OWNER_ID, ownerId.toString());
            tag.putString(DIMENSION, dimension);
            tag.putInt(X, pos.getX());
            tag.putInt(Y, pos.getY());
            tag.putInt(Z, pos.getZ());
        });
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public static void clear(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            tag.remove(PENDING);
            tag.remove(LINK_ID);
            tag.remove(OWNER_ID);
            tag.remove(DIMENSION);
            tag.remove(X);
            tag.remove(Y);
            tag.remove(Z);
            if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
            else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
    }

    public static Optional<Data> find(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(ModItems.END_PORTAL_GENERATOR.get())) continue;
            Optional<Data> data = get(stack);
            if (data.isPresent()) return data;
        }
        return Optional.empty();
    }

    public static boolean sourceExists(MinecraftServer server, Data data) {
        if (server == null || !Level.OVERWORLD.identifier().toString().equals(data.dimension())) return false;
        var level = server.getLevel(Level.OVERWORLD);
        if (level == null) return false;
        return level.getBlockEntity(data.pos()) instanceof EndPortalGeneratorBlockEntity blockEntity && blockEntity.matches(data.linkId(), data.ownerId());
    }

    public static void clearOwnerStack(MinecraftServer server, UUID ownerId, UUID linkId) {
        if (server == null) return;
        ServerPlayer player = server.getPlayerList().getPlayer(ownerId);
        if (player == null) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(ModItems.END_PORTAL_GENERATOR.get())) continue;
            Optional<Data> data = get(stack);
            if (data.isPresent() && data.get().linkId().equals(linkId)) {
                clear(stack);
                player.getInventory().setChanged();
                return;
            }
        }
    }
}
