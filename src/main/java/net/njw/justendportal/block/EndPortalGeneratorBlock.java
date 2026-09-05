package net.njw.justendportal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.network.PendingStateSync;
import net.njw.justendportal.util.PendingGeneratorData;

public class EndPortalGeneratorBlock extends Block implements EntityBlock {
    public EndPortalGeneratorBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new EndPortalGeneratorBlockEntity(pos, state); }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EndPortalGeneratorBlockEntity blockEntity && blockEntity.getOwnerId() != null && blockEntity.getLinkId() != null && level.getServer() != null) {
            PendingPortalSavedData.get(level.getServer()).clear(blockEntity.getOwnerId(), blockEntity.getLinkId());
            PendingGeneratorData.clearOwnerStack(level.getServer(), blockEntity.getOwnerId(), blockEntity.getLinkId());
            PendingStateSync.sendToOwner(level.getServer(), blockEntity.getOwnerId(), false);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
