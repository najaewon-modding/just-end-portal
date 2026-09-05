package net.njw.justendportal.event;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.util.PendingGeneratorData;

public final class GameplayEvents {
    private GameplayEvents() {
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level().isClientSide()) return;
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof EndPortalGeneratorBlockEntity blockEntity)) return;
        if (blockEntity.getOwnerId() == null || blockEntity.getLinkId() == null) return;
        PendingGeneratorData.clearOwnerStack(event.getPlayer().getServer(), blockEntity.getOwnerId(), blockEntity.getLinkId());
    }
}
