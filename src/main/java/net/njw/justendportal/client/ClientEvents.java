package net.njw.justendportal.client;

import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.njw.justendportal.JustEndPortal;
import net.njw.justendportal.client.model.GeneratorBobProperty;
import net.njw.justendportal.client.renderer.EndPortalGeneratorRenderer;
import net.njw.justendportal.network.PendingClientState;
import net.njw.justendportal.network.PendingStatePayload;
import net.njw.justendportal.registry.ModBlockEntities;

@EventBusSubscriber(modid = JustEndPortal.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.END_PORTAL_GENERATOR.get(), EndPortalGeneratorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.LINKED_END_PORTAL.get(), context -> new TheEndPortalRenderer());
    }

    @SubscribeEvent
    public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) { event.register(Identifier.fromNamespaceAndPath(JustEndPortal.MODID, "generator_bob"), GeneratorBobProperty.MAP_CODEC); }

    @SubscribeEvent
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) { event.register(PendingStatePayload.TYPE, (payload, context) -> PendingClientState.setPending(payload.pending())); }
}
