package net.njw.justendportal.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.data.PendingPortalSavedData.Cell;
import net.njw.justendportal.registry.ModBlocks;

public final class PortalExpansion {
    public enum Result { SUCCESS, NOT_OWNER, TOO_LARGE, OPPOSITE_BLOCKED, INVALID }
    private PortalExpansion() {}

    public static boolean hasAdjacentPortal(Level level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) if (level.getBlockState(pos.relative(direction)).is(ModBlocks.LINKED_END_PORTAL.get())) return true;
        return false;
    }

    public static Result expand(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (level.dimension() != Level.OVERWORLD && level.dimension() != Level.END) return Result.INVALID;
        String dimension = level.dimension().identifier().toString();
        var saved = PendingPortalSavedData.get(level.getServer());
        var optional = saved.getEntry(player.getUUID(), dimension);
        if (optional.isEmpty() || !optional.get().linked()) return Result.NOT_OWNER;
        var entry = optional.get();
        BlockPos origin = entry.sourcePos();
        if (pos.getY() != origin.getY()) return Result.NOT_OWNER;
        Cell cell = new Cell(pos.getX() - origin.getX(), pos.getZ() - origin.getZ());
        if (entry.cells().contains(cell)) return Result.INVALID;
        boolean adjacentOwn = entry.cells().stream().anyMatch(existing -> Math.abs(existing.x() - cell.x()) + Math.abs(existing.z() - cell.z()) == 1 && level.getBlockState(entry.sourcePos(existing)).is(ModBlocks.LINKED_END_PORTAL.get()));
        if (!adjacentOwn) return Result.NOT_OWNER;
        List<Cell> cells = new java.util.ArrayList<>(entry.cells());
        cells.add(cell);
        int minX = cells.stream().mapToInt(Cell::x).min().orElse(0), maxX = cells.stream().mapToInt(Cell::x).max().orElse(0), minZ = cells.stream().mapToInt(Cell::z).min().orElse(0), maxZ = cells.stream().mapToInt(Cell::z).max().orElse(0);
        if (maxX - minX >= 3 || maxZ - minZ >= 3) return Result.TOO_LARGE;
        ServerLevel targetLevel = level.getServer().getLevel(level.dimension() == Level.END ? Level.OVERWORLD : Level.END);
        if (targetLevel == null) return Result.INVALID;
        BlockPos targetPos = entry.targetPos(cell);
        targetLevel.getChunkAt(targetPos);
        if (!targetLevel.getBlockState(targetPos).isAir()) return Result.OPPOSITE_BLOCKED;
        BlockState oldCurrent = level.getBlockState(pos), oldTarget = targetLevel.getBlockState(targetPos);
        if (!level.setBlock(pos, ModBlocks.LINKED_END_PORTAL.get().defaultBlockState(), Block.UPDATE_ALL)) return Result.INVALID;
        if (!CustomEndPlatform.createCell(targetLevel, targetPos)) { level.setBlock(pos, oldCurrent, Block.UPDATE_ALL); return Result.INVALID; }
        if (!saved.addCell(player.getUUID(), dimension, cell)) { level.setBlock(pos, oldCurrent, Block.UPDATE_ALL); targetLevel.setBlock(targetPos, oldTarget, Block.UPDATE_ALL); return Result.INVALID; }
        return Result.SUCCESS;
    }
}
