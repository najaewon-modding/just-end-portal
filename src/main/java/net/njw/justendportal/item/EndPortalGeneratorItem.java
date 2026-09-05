package net.njw.justendportal.item;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.util.PendingGeneratorData;

public class EndPortalGeneratorItem extends BlockItem {
    public EndPortalGeneratorItem(Block block, Properties properties) { super(block, properties); }

    @Override
    public Component getName(ItemStack stack) { return PendingGeneratorData.get(stack).isPresent() ? Component.translatable("item.justendportal.end_portal_generator.active") : super.getName(stack); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;
        var pending = PendingGeneratorData.get(stack);
        if (pending.isPresent()) {
            if (level.dimension() != Level.END) return InteractionResult.FAIL;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            var data = pending.get();
            if (!data.ownerId().equals(player.getUUID()) || level.getServer() == null) return InteractionResult.FAIL;
            var saved = PendingPortalSavedData.get(level.getServer());
            if (!saved.matches(player.getUUID(), data.linkId())) { PendingGeneratorData.clear(stack); player.getInventory().setChanged(); return InteractionResult.FAIL; }
            if (!PendingGeneratorData.sourceExists(level.getServer(), data)) { saved.clear(player.getUUID(), data.linkId()); PendingGeneratorData.clear(stack); player.getInventory().setChanged(); return InteractionResult.FAIL; }
            player.setItemInHand(context.getHand(), ItemStack.EMPTY);
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        if (level.dimension() != Level.OVERWORLD) return InteractionResult.FAIL;
        if (!level.isClientSide() && level.getServer() != null && PendingPortalSavedData.get(level.getServer()).getEntry(player.getUUID()).isPresent()) return InteractionResult.FAIL;
        BlockPos placedPos = new BlockPlaceContext(context).getClickedPos();
        ItemStack retained = stack.copy();
        retained.setCount(1);
        InteractionResult result = super.useOn(context);
        if (!result.consumesAction() || level.isClientSide()) return result;
        UUID linkId = UUID.randomUUID();
        String dimension = level.dimension().identifier().toString();
        PendingGeneratorData.set(retained, linkId, player.getUUID(), dimension, placedPos);
        if (level.getBlockEntity(placedPos) instanceof EndPortalGeneratorBlockEntity blockEntity) blockEntity.setPending(linkId, player.getUUID());
        if (level.getServer() != null) PendingPortalSavedData.get(level.getServer()).put(player.getUUID(), linkId, dimension, placedPos);
        player.setItemInHand(context.getHand(), retained);
        player.getInventory().setChanged();
        return result;
    }
}
