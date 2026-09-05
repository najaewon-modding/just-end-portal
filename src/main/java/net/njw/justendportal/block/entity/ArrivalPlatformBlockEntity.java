package net.njw.justendportal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.registry.ModBlockEntities;

public class ArrivalPlatformBlockEntity extends BlockEntity {
    public ArrivalPlatformBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARRIVAL_PLATFORM.get(), pos, state); }
}
