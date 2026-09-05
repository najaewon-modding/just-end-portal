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
            var pending = PendingPortalSavedData.get(context.getSource().getServer()).getEntry(player.getUUID());
            if (pending.isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal("대기 중이거나 연결된 엔드 포탈이 없습니다."), false);
                return 0;
            }
            var data = pending.get();
            if (data.linked()) context.getSource().sendSuccess(() -> Component.literal("Linked End Portal | Overworld: " + data.pos().getX() + " " + data.pos().getY() + " " + data.pos().getZ() + " | End: " + data.endPos().getX() + " " + data.endPos().getY() + " " + data.endPos().getZ() + " | Link: " + data.linkId()), false);
            else context.getSource().sendSuccess(() -> Component.literal("Pending End Portal Generator | Dimension: " + data.dimension() + " | Position: " + data.pos().getX() + " " + data.pos().getY() + " " + data.pos().getZ() + " | Link: " + data.linkId()), false);
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
            PendingStateSync.send(player, false);
            boolean result = changed;
            context.getSource().sendSuccess(() -> Component.literal(result ? "Pending 데이터를 초기화했습니다." : "초기화할 Pending 데이터가 없습니다."), false);
            return changed ? 1 : 0;
        }))));
    }
}
