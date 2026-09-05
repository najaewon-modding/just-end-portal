package net.njw.justendportal.block.entity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.njw.justendportal.registry.ModBlockEntities;
import net.njw.justendportal.util.PendingGeneratorData;

public class EndPortalGeneratorBlockEntity extends TheEndPortalBlockEntity {
    private UUID linkId;
    private UUID ownerId;

    public EndPortalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.END_PORTAL_GENERATOR.get(), pos, state);
    }

    public void setPending(UUID linkId, UUID ownerId) {
        this.linkId = linkId;
        this.ownerId = ownerId;
        setChanged();
    }

    public boolean matches(UUID linkId, UUID ownerId) {
        return linkId.equals(this.linkId) && ownerId.equals(this.ownerId);
    }

    public UUID getLinkId() {
        return linkId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide() && ownerId != null && linkId != null) PendingGeneratorData.clearOwnerStack(level.getServer(), ownerId, linkId);
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (linkId != null) output.putString("LinkId", linkId.toString());
        if (ownerId != null) output.putString("OwnerId", ownerId.toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String link = input.getStringOr("LinkId", "");
        String owner = input.getStringOr("OwnerId", "");
        try {
            linkId = link.isEmpty() ? null : UUID.fromString(link);
            ownerId = owner.isEmpty() ? null : UUID.fromString(owner);
        } catch (IllegalArgumentException ignored) {
            linkId = null;
            ownerId = null;
        }
    }
}
