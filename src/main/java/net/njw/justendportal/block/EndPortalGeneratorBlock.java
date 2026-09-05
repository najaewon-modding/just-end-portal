package net.njw.justendportal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;

public class EndPortalGeneratorBlock extends Block implements EntityBlock {
    public EndPortalGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EndPortalGeneratorBlockEntity(pos, state);
    }
}
