package net.njw.justendportal.block;

import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.njw.justendportal.block.entity.LinkedEndPortalBlockEntity;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.network.PendingStateSync;
import net.njw.justendportal.registry.ModBlocks;
import net.njw.justendportal.util.CustomEndPlatform;
import org.jspecify.annotations.Nullable;

public class LinkedEndPortalBlock extends EndPortalBlock {
    public LinkedEndPortalBlock(BlockBehaviour.Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LinkedEndPortalBlockEntity(pos, state); }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity.canUsePortal(false)) entity.setAsInsidePortal(this, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            String dimension = serverLevel.dimension().identifier().toString();
            var saved = PendingPortalSavedData.get(serverLevel.getServer());
            var linked = saved.findSourceOwned(dimension, pos);
            if (linked.isPresent()) {
                UUID ownerId = linked.get().ownerId();
                var entry = linked.get().entry();
                UUID linkId = UUID.fromString(entry.linkId());
                ServerLevel targetLevel = serverLevel.getServer().getLevel(serverLevel.dimension() == Level.END ? Level.OVERWORLD : Level.END);
                saved.clear(ownerId, linkId);
                PendingStateSync.sendToOwner(serverLevel.getServer(), ownerId);
                for (var cell : entry.cells()) removePortalCell(serverLevel, entry.sourcePos(cell), pos);
                if (targetLevel != null) for (var cell : entry.cells()) removePlatformCell(targetLevel, entry.targetPos(cell));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static void removePortalCell(ServerLevel level, BlockPos pos, @Nullable BlockPos skipped) { if (pos.equals(skipped)) return; level.getChunkAt(pos); if (level.getBlockState(pos).is(ModBlocks.LINKED_END_PORTAL.get())) level.destroyBlock(pos, false); }
    private static void removePlatformCell(ServerLevel level, BlockPos pos) { level.getChunkAt(pos); if (level.getBlockState(pos).is(ModBlocks.ARRIVAL_PLATFORM.get())) level.destroyBlock(pos, false); }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos) {
        String dimension = currentLevel.dimension().identifier().toString();
        var linked = PendingPortalSavedData.get(currentLevel.getServer()).findSourceOwned(dimension, portalEntryPos);
        if (linked.isEmpty()) return null;
        var entry = linked.get().entry();
        var cell = linked.get().cell();
        boolean fromEnd = currentLevel.dimension() == Level.END;
        ServerLevel destinationLevel = currentLevel.getServer().getLevel(fromEnd ? Level.OVERWORLD : Level.END);
        if (destinationLevel == null) return null;
        BlockPos destinationFloor = entry.targetPos(cell);
        destinationLevel.getChunkAt(destinationFloor);
        CustomEndPlatform.prepareArrival(destinationLevel, entry);
        if (!destinationLevel.getBlockState(destinationFloor).is(ModBlocks.ARRIVAL_PLATFORM.get())) return null;
        Vec3 position = Vec3.atBottomCenterOf(destinationFloor).add(0.0, !fromEnd && !(entity instanceof ServerPlayer) ? 2.0 : 1.0, 0.0);
        float yRot;
        float xRot;
        Set<Relative> relatives;
        if (!fromEnd) {
            yRot = Direction.WEST.toYRot();
            xRot = 0.0F;
            relatives = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT));
        } else {
            yRot = 0.0F;
            xRot = 0.0F;
            relatives = Relative.union(Relative.DELTA, Relative.ROTATION);
        }
        return new TeleportTransition(destinationLevel, position, Vec3.ZERO, yRot, xRot, relatives, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
    }
}
