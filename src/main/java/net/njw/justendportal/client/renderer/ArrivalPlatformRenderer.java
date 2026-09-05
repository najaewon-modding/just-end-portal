package net.njw.justendportal.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.njw.justendportal.block.entity.ArrivalPlatformBlockEntity;
import org.jspecify.annotations.Nullable;

public class ArrivalPlatformRenderer implements BlockEntityRenderer<ArrivalPlatformBlockEntity, ArrivalPlatformRenderer.State> {
    public static class State extends BlockEntityRenderState { public ClientLevel level; }
    public ArrivalPlatformRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(ArrivalPlatformBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.level = blockEntity.getLevel() instanceof ClientLevel clientLevel ? clientLevel : null;
    }
    @Override public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.level == null) return;
        float seconds = (float) (System.nanoTime() / 1_000_000_000.0);
        float bob = Mth.sin(seconds * 2.4F) * 0.04F;
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(obsidian);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(), parts);
        if (parts.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F + bob, 0.0F);
        poseStack.scale(1.0F, 0.5F, 1.0F);
        submitGeometry(state, poseStack, collector, obsidian, model, Sheets.cutoutBlockSheet());
        submitGeometry(state, poseStack, collector, obsidian, model, RenderTypes.glint());
        poseStack.popPose();
    }
    private static void submitGeometry(State state, PoseStack poseStack, SubmitNodeCollector collector, BlockState blockState, BlockStateModel model, RenderType renderType) {
        collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            ModelBlockRenderer renderer = new ModelBlockRenderer(true, false, Minecraft.getInstance().getBlockColors());
            renderer.tesselateBlock((x, y, z, quad, instance) -> buffer.putBakedQuad(pose, quad, instance), 0, 0, 0, state.level, state.blockPos, blockState, model, blockState.getSeed(state.blockPos));
        });
    }
}
