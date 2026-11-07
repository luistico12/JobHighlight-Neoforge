package com.example.jobhighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** Zeichnet die markierten Bloecke als Rahmen (durch Waende sichtbar). Nur auf dem Client geladen. */
@EventBusSubscriber(modid = JobHighlight.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class HighlightRenderer {
    private HighlightRenderer() {}

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        List<BlockPos> positions = ClientHighlights.active();
        if (positions.isEmpty()) {
            return;
        }

        int color = ClientHighlights.color();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(HighlightRenderTypes.LINES_NO_DEPTH);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        for (BlockPos pos : positions) {
            LevelRenderer.renderLineBox(poseStack, lines, new AABB(pos).inflate(0.002D), red, green, blue, 1.0F);
        }
        poseStack.popPose();

        buffers.endBatch(HighlightRenderTypes.LINES_NO_DEPTH);
    }
}
