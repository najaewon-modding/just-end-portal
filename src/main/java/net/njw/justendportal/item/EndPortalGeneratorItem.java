package net.njw.justendportal.item;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.njw.justendportal.util.CustomEndPlatform;
import net.njw.justendportal.util.PendingGeneratorData;
import net.njw.justendportal.util.PortalExpansion;

public class EndPortalGeneratorItem extends BlockItem {
    public EndPortalGeneratorItem(Block block, Properties properties) { super(block, properties); }
    @Override public Component getName(ItemStack stack) { return PendingGeneratorData.get(stack).isPresent() ? Component.translatable("item.njw_just_end_portal.end_portal_generator.awaiting_link") : super.getName(stack); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;
        String currentDimension = level.dimension().identifier().toString();
        var pending = PendingGeneratorData.get(stack);
        if (pending.isPresent()) {
            var data = pending.get();
            String targetDimension = Level.OVERWORLD.identifier().toString().equals(data.dimension()) ? Level.END.identifier().toString() : Level.END.identifier().toString().equals(data.dimension()) ? Level.OVERWORLD.identifier().toString() : "";
            if (!currentDimension.equals(targetDimension)) {
                player.sendOverlayMessage(Component.translatable("message.njw_just_end_portal.awaiting_link_opposite_only"));
                return InteractionResult.FAIL;
            }
            BlockPlaceContext placeContext = new BlockPlaceContext(context);
            BlockState placementState = getPlacementState(placeContext);
            if (placementState == null || !canPlace(placeContext, placementState)) return InteractionResult.FAIL;
            BlockPos targetPos = placeContext.getClickedPos();
            if (!level.getBlockState(targetPos).isAir()) return InteractionResult.FAIL;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (!data.ownerId().equals(player.getUUID()) || level.getServer() == null || !(level instanceof ServerLevel targetLevel)) return InteractionResult.FAIL;
            var saved = PendingPortalSavedData.get(level.getServer());
            var source = saved.getEntry(player.getUUID(), data.dimension());
            if (source.isEmpty() || !source.get().linkId().equals(data.linkId().toString()) || source.get().linked()) {
                PendingGeneratorData.clear(stack);
                if (player instanceof ServerPlayer serverPlayer) PendingStateSync.send(serverPlayer);
                player.getInventory().setChanged();
                return InteractionResult.FAIL;
            }
            ServerLevel sourceLevel = Level.END.identifier().toString().equals(data.dimension()) ? level.getServer().getLevel(Level.END) : level.getServer().getLevel(Level.OVERWORLD);
            if (sourceLevel == null) return InteractionResult.FAIL;
            BlockPos sourcePos = source.get().sourcePos();
            sourceLevel.getChunkAt(sourcePos);
            if (!(sourceLevel.getBlockEntity(sourcePos) instanceof EndPortalGeneratorBlockEntity blockEntity) || !blockEntity.matches(data.linkId(), player.getUUID())) return InteractionResult.FAIL;
            BlockState oldTargetState = targetLevel.getBlockState(targetPos), oldSourceState = sourceLevel.getBlockState(sourcePos);
            if (!CustomEndPlatform.createCell(targetLevel, targetPos)) return InteractionResult.FAIL;
            if (!sourceLevel.setBlock(sourcePos, ModBlocks.LINKED_END_PORTAL.get().defaultBlockState(), Block.UPDATE_ALL)) {
                targetLevel.setBlock(targetPos, oldTargetState, Block.UPDATE_ALL);
                return InteractionResult.FAIL;
            }
            if (!saved.link(player.getUUID(), data.linkId(), targetPos)) {
                sourceLevel.setBlock(sourcePos, oldSourceState, Block.UPDATE_ALL);
                targetLevel.setBlock(targetPos, oldTargetState, Block.UPDATE_ALL);
                return InteractionResult.FAIL;
            }
            sourceLevel.playSound(null, sourcePos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
            targetLevel.playSound(null, targetPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(context.getHand(), ItemStack.EMPTY);
            player.getInventory().setChanged();
            if (player instanceof ServerPlayer serverPlayer) PendingStateSync.send(serverPlayer);
            return InteractionResult.SUCCESS;
        }

        if (level.dimension() != Level.OVERWORLD && level.dimension() != Level.END) return InteractionResult.FAIL;
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos placedPos = placeContext.getClickedPos();
        if (PortalExpansion.hasAdjacentPortal(level, placedPos)) {
            BlockState placementState = getPlacementState(placeContext);
            if (placementState == null || !canPlace(placeContext, placementState)) return InteractionResult.FAIL;
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;
            PortalExpansion.Result expansion = PortalExpansion.expand(serverLevel, placedPos, serverPlayer);
            if (expansion == PortalExpansion.Result.SUCCESS) {
                stack.shrink(1);
                player.getInventory().setChanged();
                return InteractionResult.SUCCESS;
            }
            if (expansion == PortalExpansion.Result.NOT_OWNER) player.sendOverlayMessage(Component.translatable("message.njw_just_end_portal.expansion_not_owner"));
            else if (expansion == PortalExpansion.Result.TOO_LARGE) player.sendOverlayMessage(Component.translatable("message.njw_just_end_portal.expansion_too_large"));
            else if (expansion == PortalExpansion.Result.OPPOSITE_BLOCKED) player.sendOverlayMessage(Component.translatable("message.njw_just_end_portal.expansion_opposite_blocked"));
            return InteractionResult.FAIL;
        }

        boolean alreadyInstalled = level.isClientSide() ? PendingClientState.hasEntry(level.dimension()) || PendingGeneratorData.find(player, currentDimension).isPresent() : level.getServer() != null && PendingPortalSavedData.get(level.getServer()).hasEntry(player.getUUID(), currentDimension);
        if (alreadyInstalled) {
            player.sendOverlayMessage(Component.translatable("message.njw_just_end_portal.portal_limit_dimension"));
            return InteractionResult.FAIL;
        }
        ItemStack retained = stack.copy();
        retained.setCount(1);
        InteractionResult result = super.useOn(context);
        if (!result.consumesAction() || level.isClientSide()) return result;
        UUID linkId = UUID.randomUUID();
        PendingGeneratorData.set(retained, linkId, player.getUUID(), currentDimension, placedPos);
        if (level.getBlockEntity(placedPos) instanceof EndPortalGeneratorBlockEntity blockEntity) blockEntity.setPending(linkId, player.getUUID());
        if (level.getServer() != null) PendingPortalSavedData.get(level.getServer()).put(player.getUUID(), linkId, currentDimension, placedPos);
        if (player instanceof ServerPlayer serverPlayer) PendingStateSync.send(serverPlayer);
        player.setItemInHand(context.getHand(), retained);
        player.getInventory().setChanged();
        return result;
    }
}
