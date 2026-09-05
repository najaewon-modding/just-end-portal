package net.njw.justendportal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.registry.ModBlockEntities;

public class EndPortalGeneratorBlockEntity extends TheEndPortalBlockEntity {
    public EndPortalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.END_PORTAL_GENERATOR.get(), pos, state);
    }
}
