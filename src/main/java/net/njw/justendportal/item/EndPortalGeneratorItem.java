package net.njw.justendportal.item;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.data.PendingPortalSavedData;
import net.njw.justendportal.network.PendingClientState;
import net.njw.justendportal.network.PendingStateSync;
import net.njw.justendportal.registry.ModBlocks;
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
            BlockPlaceContext placeContext = new BlockPlaceContext(context);
            BlockState placementState = getPlacementState(placeContext);
            if (placementState == null || !canPlace(placeContext, placementState)) return InteractionResult.FAIL;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            var data = pending.get();
            if (!data.ownerId().equals(player.getUUID()) || level.getServer() == null) return InteractionResult.FAIL;
            var saved = PendingPortalSavedData.get(level.getServer());
            var source = saved.getEntry(player.getUUID());
            if (source.isEmpty() || !source.get().linkId().equals(data.linkId().toString()) || source.get().linked()) {
                PendingGeneratorData.clear(stack);
                if (player instanceof ServerPlayer serverPlayer) PendingStateSync.send(serverPlayer, source.isPresent());
                player.getInventory().setChanged();
                return InteractionResult.FAIL;
            }
            var overworld = level.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null) return InteractionResult.FAIL;
            BlockPos sourcePos = source.get().pos();
            overworld.getChunkAt(sourcePos);
            if (!(overworld.getBlockEntity(sourcePos) instanceof EndPortalGeneratorBlockEntity blockEntity) || !blockEntity.matches(data.linkId(), player.getUUID())) return InteractionResult.FAIL;
            BlockPos endPos = placeContext.getClickedPos();
            BlockState oldEndState = level.getBlockState(endPos);
            BlockState oldSourceState = overworld.getBlockState(sourcePos);
            if (!level.setBlock(endPos, ModBlocks.LINKED_END_PORTAL.get().defaultBlockState(), Block.UPDATE_ALL)) return InteractionResult.FAIL;
            if (!overworld.setBlock(sourcePos, ModBlocks.LINKED_END_PORTAL.get().defaultBlockState(), Block.UPDATE_ALL)) {
                level.setBlock(endPos, oldEndState, Block.UPDATE_ALL);
                return InteractionResult.FAIL;
            }
            if (!saved.link(player.getUUID(), data.linkId(), endPos)) {
                overworld.setBlock(sourcePos, oldSourceState, Block.UPDATE_ALL);
                level.setBlock(endPos, oldEndState, Block.UPDATE_ALL);
                return InteractionResult.FAIL;
            }
            player.setItemInHand(context.getHand(), ItemStack.EMPTY);
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        if (level.dimension() != Level.OVERWORLD) return InteractionResult.FAIL;
        boolean alreadyInstalled = level.isClientSide() ? PendingClientState.hasPending() || PendingGeneratorData.find(player).isPresent() : level.getServer() != null && PendingPortalSavedData.get(level.getServer()).getEntry(player.getUUID()).isPresent();
        if (alreadyInstalled) {
            player.sendOverlayMessage(Component.translatable("message.justendportal.portal_limit"));
            return InteractionResult.FAIL;
        }
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
        if (player instanceof ServerPlayer serverPlayer) PendingStateSync.send(serverPlayer, true);
        player.setItemInHand(context.getHand(), retained);
        player.getInventory().setChanged();
        return result;
    }
}
