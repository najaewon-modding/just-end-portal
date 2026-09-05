package net.njw.justendportal;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.njw.justendportal.registry.ModBlockEntities;
import net.njw.justendportal.registry.ModBlocks;
import net.njw.justendportal.registry.ModItems;
import org.slf4j.Logger;

@Mod(JustEndPortal.MODID)
public class JustEndPortal {
    public static final String MODID = "justendportal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustEndPortal(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(ModItems.END_PORTAL_GENERATOR.get());
    }
}
