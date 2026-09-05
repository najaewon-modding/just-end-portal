package net.njw.justendportal.registry;

import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.block.EndPortalGeneratorBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JustEndPortal.MODID);
    public static final DeferredBlock<EndPortalGeneratorBlock> END_PORTAL_GENERATOR = BLOCKS.registerBlock("end_portal_generator", EndPortalGeneratorBlock::new, properties -> properties.destroyTime(5.0F).explosionResistance(1200.0F).sound(SoundType.STONE).lightLevel(state -> 15).noOcclusion());

    private ModBlocks() {
    }
}
