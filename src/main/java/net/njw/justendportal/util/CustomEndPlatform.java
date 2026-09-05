package net.njw.justendportal.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.njw.justendportal.registry.ModBlocks;

public final class CustomEndPlatform {
    public static final int RADIUS = 2;
    public static final int CLEAR_HEIGHT = 3;
    private CustomEndPlatform() {}

    public static void createFloor(ServerLevel level, BlockPos origin) {
        for (int x = -RADIUS; x <= RADIUS; x++) for (int z = -RADIUS; z <= RADIUS; z++) {
            BlockPos floor = origin.offset(x, -1, z);
            if (!level.getBlockState(floor).is(ModBlocks.ARRIVAL_PLATFORM.get())) {
                if (!level.getBlockState(floor).isAir()) level.destroyBlock(floor, false);
                level.setBlock(floor, ModBlocks.ARRIVAL_PLATFORM.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static void prepareArrival(ServerLevel level, BlockPos origin) {
        createFloor(level, origin);
        for (int x = -RADIUS; x <= RADIUS; x++) for (int z = -RADIUS; z <= RADIUS; z++) for (int y = 0; y < CLEAR_HEIGHT; y++) {
            BlockPos clear = origin.offset(x, y, z);
            if (!level.getBlockState(clear).isAir()) {
                level.destroyBlock(clear, false);
                if (!level.getBlockState(clear).isAir()) level.setBlock(clear, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
