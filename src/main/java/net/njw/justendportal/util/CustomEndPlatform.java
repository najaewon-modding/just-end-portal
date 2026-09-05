package net.njw.justendportal.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.njw.justendportal.data.PendingPortalSavedData.Entry;
import net.njw.justendportal.registry.ModBlocks;

public final class CustomEndPlatform {
    public static final int CLEAR_HEIGHT = 3;
    private CustomEndPlatform() {}

    public static boolean canCreateCell(ServerLevel level, BlockPos floorPos) { return level.getBlockState(floorPos).isAir() || level.getBlockState(floorPos).is(ModBlocks.ARRIVAL_PLATFORM.get()); }
    public static boolean createCell(ServerLevel level, BlockPos floorPos) { if (!canCreateCell(level, floorPos)) return false; return level.getBlockState(floorPos).is(ModBlocks.ARRIVAL_PLATFORM.get()) || level.setBlock(floorPos, ModBlocks.ARRIVAL_PLATFORM.get().defaultBlockState(), Block.UPDATE_ALL); }
    public static void prepareArrival(ServerLevel level, Entry entry) {
        for (var cell : entry.cells()) {
            BlockPos floor = entry.targetPos(cell);
            if (!level.getBlockState(floor).is(ModBlocks.ARRIVAL_PLATFORM.get())) level.setBlock(floor, ModBlocks.ARRIVAL_PLATFORM.get().defaultBlockState(), Block.UPDATE_ALL);
            for (int y = 1; y <= CLEAR_HEIGHT; y++) {
                BlockPos clear = floor.above(y);
                if (!level.getBlockState(clear).isAir()) {
                    level.destroyBlock(clear, false);
                    if (!level.getBlockState(clear).isAir()) level.setBlock(clear, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }
}
