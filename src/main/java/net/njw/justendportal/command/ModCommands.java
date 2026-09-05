package net.njw.justendportal.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.njw.justendportal.data.PendingPortalSavedData;

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
        })));
    }
}
