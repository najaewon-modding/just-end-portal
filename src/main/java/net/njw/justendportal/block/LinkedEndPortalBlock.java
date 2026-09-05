package net.njw.justendportal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.njw.justendportal.block.entity.LinkedEndPortalBlockEntity;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.registry.ModBlocks;
import org.jspecify.annotations.Nullable;

public class LinkedEndPortalBlock extends EndPortalBlock {
    public LinkedEndPortalBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LinkedEndPortalBlockEntity(pos, state); }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos) {
        String dimension = currentLevel.dimension().identifier().toString();
        var linked = PendingPortalSavedData.get(currentLevel.getServer()).findLinked(dimension, portalEntryPos);
        if (linked.isEmpty()) return null;
        var entry = linked.get();
        boolean fromOverworld = entry.dimension().equals(dimension) && entry.pos().equals(portalEntryPos);
        boolean fromEnd = Level.END.identifier().toString().equals(dimension) && entry.endPos().equals(portalEntryPos);
        if (!fromOverworld && !fromEnd) return null;
        ServerLevel destinationLevel = currentLevel.getServer().getLevel(fromOverworld ? Level.END : Level.OVERWORLD);
        if (destinationLevel == null) return null;
        BlockPos destination = fromOverworld ? entry.endPos() : entry.pos();
        destinationLevel.getChunkAt(destination);
        if (!destinationLevel.getBlockState(destination).is(ModBlocks.LINKED_END_PORTAL.get())) return null;
        Vec3 position = Vec3.atBottomCenterOf(destination).add(0.0, 1.0, 0.0);
        return new TeleportTransition(destinationLevel, position, Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
    }
}
