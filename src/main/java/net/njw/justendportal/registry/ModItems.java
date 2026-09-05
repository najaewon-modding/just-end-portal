package net.njw.justendportal.registry;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justendportal.JustEndPortal;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JustEndPortal.MODID);
    public static final DeferredItem<BlockItem> END_PORTAL_GENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.END_PORTAL_GENERATOR);

    private ModItems() {
    }
}
