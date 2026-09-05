package net.njw.justendportal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.registry.ModBlockEntities;

public class LinkedEndPortalBlockEntity extends TheEndPortalBlockEntity {
    public LinkedEndPortalBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.LINKED_END_PORTAL.get(), pos, state); }
}
