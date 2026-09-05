package net.njw.justendportal.registry;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.item.EndPortalGeneratorItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JustEndPortal.MODID);
    public static final DeferredItem<EndPortalGeneratorItem> END_PORTAL_GENERATOR = ITEMS.registerItem("end_portal_generator", properties -> new EndPortalGeneratorItem(ModBlocks.END_PORTAL_GENERATOR.get(), properties.stacksTo(1).useBlockDescriptionPrefix()));

    private ModItems() {
    }
}
