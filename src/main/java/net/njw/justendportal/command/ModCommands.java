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
            if (pending.isEmpty()) { context.getSource().sendSuccess(() -> Component.literal("대기 중인 엔드 포탈 생성기가 없습니다."), false); return 0; }
            var data = pending.get();
            context.getSource().sendSuccess(() -> Component.literal("Pending End Portal Generator | Dimension: " + data.dimension() + " | Position: " + data.x() + " " + data.y() + " " + data.z() + " | Link: " + data.linkId()), false);
            return 1;
        })));
    }
}
