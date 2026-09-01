package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.FleetingMemorialBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class FleetingMemorialRenderer implements BlockEntityRenderer<FleetingMemorialBlockEntity> {

    private static final float BOB_HEIGHT = 0f;
    private static final float BOB_AMPLITUDE = 1 / 32f;
    private static final float BOB_SPEED = 0.16f;
    private static final float TEXT_SCALE = 0.01f;
    private static final int MAX_TEXT_WIDTH = 52;
    private static final int TEXT_COLOR = 0xFF813C9E;

    private final Font font;

    public FleetingMemorialRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(FleetingMemorialBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final var blockState = blockEntity.getBlockState();
        final var level = blockEntity.getLevel();
        final double gameTime = (level != null ? level.getGameTime() : 0L) + (double) partialTicks;
        final double phase = (blockEntity.getBlockPos().asLong() & 0xffff) * 0.01;
        final var bobOffset = isTargeted(blockEntity) ? BOB_HEIGHT : BOB_HEIGHT + (float) Math.sin(gameTime * BOB_SPEED + phase) * BOB_AMPLITUDE;

        poseStack.pushPose();
        poseStack.translate(0f, bobOffset, 0f);
        final var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        final var model = blockRenderer.getBlockModel(blockState);
        final var vertexConsumer = buffer.getBuffer(RenderType.cutout());
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), vertexConsumer, blockState, model, 1f, 1f, 1f, packedLight, OverlayTexture.NO_OVERLAY);
        renderOwnerName(blockEntity.getOwnerName(), blockState.getValue(WaystoneBlockBase.FACING).toYRot(), poseStack, buffer);
        poseStack.popPose();
    }

    private static boolean isTargeted(FleetingMemorialBlockEntity blockEntity) {
        final var hitResult = Minecraft.getInstance().hitResult;
        return hitResult != null
                && hitResult.getType() == HitResult.Type.BLOCK
                && ((BlockHitResult) hitResult).getBlockPos().equals(blockEntity.getBlockPos());
    }

    private void renderOwnerName(Component ownerName, float yRot, PoseStack poseStack, MultiBufferSource buffer) {
        final var lines = splitOwnerName(ownerName);
        if (lines.isEmpty()) {
            return;
        }

        final var y = -lines.size() * font.lineHeight / 2f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.55f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0f, 0f, 1 / 16f);
        poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        final var pose = poseStack.last().pose();
        for (int i = 0; i < lines.size(); i++) {
            final var text = lines.get(i);
            final var x = -font.width(text) / 2f;
            font.drawInBatch(text, x, y + i * font.lineHeight, TEXT_COLOR, false, pose, buffer, Font.DisplayMode.POLYGON_OFFSET, 0, 15728880);
        }
        poseStack.popPose();
    }

    private List<FormattedCharSequence> splitOwnerName(Component ownerName) {
        final var text = ownerName.getString().trim();
        final var lines = new ArrayList<FormattedCharSequence>();
        var start = 0;
        while (start < text.length()) {
            final var end = findLineEnd(text, start);
            lines.add(Component.literal(text.substring(start, end).trim()).getVisualOrderText());
            start = skipSpaces(text, end);
        }

        return lines;
    }

    private int findLineEnd(String text, int start) {
        var fallbackEnd = start;
        var bestSpaceEnd = -1;
        var bestUpperCaseEnd = -1;

        for (var i = start; i < text.length(); i = text.offsetByCodePoints(i, 1)) {
            final var next = text.offsetByCodePoints(i, 1);
            if (font.width(text.substring(start, next)) > MAX_TEXT_WIDTH) {
                if (bestSpaceEnd != -1) {
                    return bestSpaceEnd;
                } else if (bestUpperCaseEnd != -1) {
                    return bestUpperCaseEnd;
                } else if (fallbackEnd > start) {
                    return fallbackEnd;
                }

                return next;
            }

            fallbackEnd = next;

            if (Character.isWhitespace(text.codePointAt(i))) {
                bestSpaceEnd = i;
            } else if (i > start && isUpperCaseBreak(text, i)) {
                bestUpperCaseEnd = i;
            }
        }

        return text.length();
    }

    private static boolean isUpperCaseBreak(String text, int index) {
        final var codePoint = text.codePointAt(index);
        if (!Character.isUpperCase(codePoint)) {
            return false;
        }

        final var previousIndex = text.offsetByCodePoints(index, -1);
        final var previousCodePoint = text.codePointAt(previousIndex);
        return Character.isLetterOrDigit(previousCodePoint);
    }

    private static int skipSpaces(String text, int index) {
        while (index < text.length() && Character.isWhitespace(text.codePointAt(index))) {
            index = text.offsetByCodePoints(index, 1);
        }

        return index;
    }
}
