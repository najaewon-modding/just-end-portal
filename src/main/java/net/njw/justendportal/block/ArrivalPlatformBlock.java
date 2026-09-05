package net.njw.justendportal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.block.entity.ArrivalPlatformBlockEntity;

public class ArrivalPlatformBlock extends Block implements EntityBlock {
    public ArrivalPlatformBlock(BlockBehaviour.Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArrivalPlatformBlockEntity(pos, state); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) != 0) return;
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
        double y = pos.getY() + 0.55 + (random.nextDouble() - 0.5) * 0.3;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
        level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0.0, 0.02, 0.0);
    }
}
