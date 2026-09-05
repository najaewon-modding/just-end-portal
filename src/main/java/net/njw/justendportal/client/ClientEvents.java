package net.njw.justendportal.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.client.renderer.EndPortalGeneratorRenderer;
import net.njw.justendportal.registry.ModBlockEntities;

@EventBusSubscriber(modid = JustEndPortal.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.END_PORTAL_GENERATOR.get(), EndPortalGeneratorRenderer::new);
    }
}
