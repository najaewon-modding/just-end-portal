package net.njw.justendportal.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.njw.justendportal.block.entity.EndPortalGeneratorBlockEntity;

public class EndPortalGeneratorRenderer extends AbstractEndPortalRenderer<EndPortalGeneratorBlockEntity, EndPortalRenderState> {
    public EndPortalGeneratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public EndPortalRenderState createRenderState() {
        return new EndPortalRenderState();
    }

    @Override
    public void submit(EndPortalRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.6F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.scale(0.28F, 0.28F, 0.28F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        submitSpecial(RenderTypes.endPortal(), poseStack, collector);
        poseStack.popPose();
    }
}
