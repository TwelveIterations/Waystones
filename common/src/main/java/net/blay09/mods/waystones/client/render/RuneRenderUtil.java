package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;

public class RuneRenderUtil {

    public static void render(BakedModel model, PoseStack poseStack, MultiBufferSource buffer, int color, int packedLight, boolean glow) {
        final var red = (color >> 16 & 0xFF) / 255f;
        final var green = (color >> 8 & 0xFF) / 255f;
        final var blue = (color & 0xFF) / 255f;
        final var vertexConsumer = buffer.getBuffer(RenderType.cutout());
        final var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), vertexConsumer, null, model, red, green, blue, glow ? 15728880 : packedLight, OverlayTexture.NO_OVERLAY);
    }

}
