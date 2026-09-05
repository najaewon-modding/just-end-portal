package net.njw.justendportal.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;
import net.njw.justendportal.block.entity.LinkedEndPortalBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JustEndPortal.MODID);
    public static final Supplier<BlockEntityType<EndPortalGeneratorBlockEntity>> END_PORTAL_GENERATOR = BLOCK_ENTITY_TYPES.register("end_portal_generator", () -> new BlockEntityType<>(EndPortalGeneratorBlockEntity::new, false, ModBlocks.END_PORTAL_GENERATOR.get()));
    public static final Supplier<BlockEntityType<LinkedEndPortalBlockEntity>> LINKED_END_PORTAL = BLOCK_ENTITY_TYPES.register("linked_end_portal", () -> new BlockEntityType<>(LinkedEndPortalBlockEntity::new, false, ModBlocks.LINKED_END_PORTAL.get()));

    private ModBlockEntities() {}
}
