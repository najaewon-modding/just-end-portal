package net.njw.justendportal.registry;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.block.EndPortalGeneratorBlock;
import net.njw.justendportal.block.LinkedEndPortalBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JustEndPortal.MODID);
    public static final DeferredBlock<EndPortalGeneratorBlock> END_PORTAL_GENERATOR = BLOCKS.registerBlock("end_portal_generator", EndPortalGeneratorBlock::new, properties -> properties.destroyTime(5.0F).explosionResistance(1200.0F).sound(SoundType.STONE).lightLevel(state -> 15).noOcclusion());
    public static final DeferredBlock<LinkedEndPortalBlock> LINKED_END_PORTAL = BLOCKS.registerBlock("linked_end_portal", LinkedEndPortalBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL).destroyTime(2.0F).sound(SoundType.GLASS));

    private ModBlocks() {}
}
