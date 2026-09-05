package net.njw.justendportal.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.network.PendingStateSync;
import net.njw.justendportal.registry.ModItems;
import net.njw.justendportal.util.PendingGeneratorData;

public final class ModCommands {
    private ModCommands() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("justendportal").then(Commands.literal("pending").executes(context -> {
            var player = context.getSource().getPlayerOrException();
            var entries = PendingPortalSavedData.get(context.getSource().getServer()).getEntries(player.getUUID());
            if (entries.isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal("대기 중이거나 생성된 엔드 포탈이 없습니다."), false);
                return 0;
            }
            for (var data : entries) {
                String source = data.dimension();
                if (data.linked()) context.getSource().sendSuccess(() -> Component.literal("Directional End Portal | Source Dimension: " + source + " | Portal: " + data.sourcePos().getX() + " " + data.sourcePos().getY() + " " + data.sourcePos().getZ() + " | Platform: " + data.targetPos().getX() + " " + data.targetPos().getY() + " " + data.targetPos().getZ() + " | Cells: " + data.cells().size() + " | Link: " + data.linkId()), false);
                else context.getSource().sendSuccess(() -> Component.literal("Pending End Portal Generator | Source Dimension: " + source + " | Position: " + data.sourcePos().getX() + " " + data.sourcePos().getY() + " " + data.sourcePos().getZ() + " | Link: " + data.linkId()), false);
            }
            return 1;
        }).then(Commands.literal("reset").executes(context -> {
            var player = context.getSource().getPlayerOrException();
            var saved = PendingPortalSavedData.get(context.getSource().getServer());
            boolean changed = saved.clear(player.getUUID());
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                var stack = player.getInventory().getItem(i);
                if (!stack.is(ModItems.END_PORTAL_GENERATOR.get()) || PendingGeneratorData.get(stack).isEmpty()) continue;
                PendingGeneratorData.clear(stack);
                changed = true;
            }
            player.getInventory().setChanged();
            PendingStateSync.send(player);
            boolean result = changed;
            context.getSource().sendSuccess(() -> Component.literal(result ? "Pending 데이터를 초기화했습니다." : "초기화할 Pending 데이터가 없습니다."), false);
            return changed ? 1 : 0;
        }))));
    }
}
