package net.njw.justendportal.item;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.util.PendingGeneratorData;

public class EndPortalGeneratorItem extends BlockItem {
    public EndPortalGeneratorItem(Block block, Properties properties) {
        super(block, properties);
    }

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
            if (!data.ownerId().equals(player.getUUID())) return InteractionResult.FAIL;
            if (!PendingGeneratorData.sourceExists(level.getServer(), data)) {
                PendingGeneratorData.clear(stack);
                player.getInventory().setChanged();
                return InteractionResult.FAIL;
            }
            stack.setCount(0);
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        if (level.dimension() != Level.OVERWORLD) return InteractionResult.FAIL;
        if (PendingGeneratorData.find(player).isPresent()) return InteractionResult.FAIL;
        BlockPos placedPos = new BlockPlaceContext(context).getClickedPos();
        InteractionResult result = super.useOn(context);
        if (!result.consumesAction() || level.isClientSide()) return result;
        stack.setCount(1);
        UUID linkId = UUID.randomUUID();
        PendingGeneratorData.set(stack, linkId, player.getUUID(), level.dimension().identifier().toString(), placedPos);
        if (level.getBlockEntity(placedPos) instanceof EndPortalGeneratorBlockEntity blockEntity) blockEntity.setPending(linkId, player.getUUID());
        player.getInventory().setChanged();
        return result;
    }
}
