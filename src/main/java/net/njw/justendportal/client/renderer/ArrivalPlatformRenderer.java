package net.njw.justendportal.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.njw.justendportal.block.entity.ArrivalPlatformBlockEntity;
import org.jspecify.annotations.Nullable;

public class ArrivalPlatformRenderer implements BlockEntityRenderer<ArrivalPlatformBlockEntity, ArrivalPlatformRenderer.State> {
    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockResolver;
    public static class State extends BlockEntityRenderState { public final BlockModelRenderState obsidian = new BlockModelRenderState(); }
    public ArrivalPlatformRenderer(BlockEntityRendererProvider.Context context) { this.blockResolver = context.blockModelResolver(); }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(ArrivalPlatformBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        blockResolver.update(state.obsidian, Blocks.OBSIDIAN.defaultBlockState(), DISPLAY_CONTEXT);
    }
    @Override public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float seconds = (float) (System.nanoTime() / 1_000_000_000.0);
        float bob = Mth.sin(seconds * 2.4F) * 0.04F;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F + bob, 0.0F);
        poseStack.scale(1.0F, 0.5F, 1.0F);
        state.obsidian.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
