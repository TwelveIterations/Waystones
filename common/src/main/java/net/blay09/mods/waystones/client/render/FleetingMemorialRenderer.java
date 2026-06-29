package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.FleetingMemorialBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FleetingMemorialRenderer implements BlockEntityRenderer<FleetingMemorialBlockEntity, FleetingMemorialRenderer.FleetingMemorialRenderState> {

    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final float BOB_HEIGHT = 0f;
    private static final float BOB_AMPLITUDE = 1 / 32f;
    private static final float BOB_SPEED = 0.16f;
    private static final float TARGETED_BOB_LERP = 0.35f;
    private static final float TEXT_SCALE = 0.01f;
    private static final int MAX_TEXT_WIDTH = 52;
    private static final int TEXT_COLOR = 0xFF813C9E;

    public static class FleetingMemorialRenderState extends BlockEntityRenderState {
        public final BlockModelRenderState model = new BlockModelRenderState();
        public final List<BlockStateModelPart> breakingParts = new ArrayList<>();
        public Direction facing = Direction.NORTH;
        public Component ownerName = Component.empty();
        public float bobOffset;
    }

    private final BlockModelResolver blockModelResolver;
    private final Font font;

    public FleetingMemorialRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
        this.font = context.font();
    }

    @Override
    public FleetingMemorialRenderState createRenderState() {
        return new FleetingMemorialRenderState();
    }

    @Override
    public void extractRenderState(FleetingMemorialBlockEntity blockEntity, FleetingMemorialRenderState renderState, float delta, Vec3 vec, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, delta, vec, crumblingOverlay);
        final var blockState = blockEntity.getBlockState();
        blockModelResolver.update(renderState.model, blockState, BLOCK_DISPLAY_CONTEXT);
        renderState.breakingParts.clear();
        if (crumblingOverlay != null) {
            final var blockModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
            final var random = renderState.model.scratchRandomSource(blockState.getSeed(blockEntity.getBlockPos()));
            blockModel.collectParts(random, renderState.breakingParts);
        }

        renderState.facing = blockState.getValue(WaystoneBlockBase.FACING);
        renderState.ownerName = blockEntity.getOwnerName();

        final var level = blockEntity.getLevel();
        final var gameTime = level != null ? level.getGameTime() + delta : delta;
        final var phase = (blockEntity.getBlockPos().asLong() & 0xffff) * 0.01f;
        final var targetBobOffset = BOB_HEIGHT + Mth.sin(gameTime * BOB_SPEED + phase) * BOB_AMPLITUDE;
        renderState.bobOffset = isTargeted(blockEntity) ? Mth.lerp(TARGETED_BOB_LERP, renderState.bobOffset, BOB_HEIGHT) : targetBobOffset;
    }

    private static boolean isTargeted(FleetingMemorialBlockEntity blockEntity) {
        final var hitResult = Minecraft.getInstance().hitResult;
        return hitResult != null
                && hitResult.getType() == HitResult.Type.BLOCK
                && ((BlockHitResult) hitResult).getBlockPos().equals(blockEntity.getBlockPos());
    }

    @Override
    public void submit(FleetingMemorialRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0f, renderState.bobOffset, 0f);
        renderState.model.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        if (renderState.breakProgress != null) {
            submitNodeCollector.submitBreakingBlockModel(poseStack, renderState.breakingParts, renderState.breakProgress.progress());
        }
        submitOwnerName(renderState, poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    private void submitOwnerName(FleetingMemorialRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        final var lines = splitOwnerName(renderState.ownerName);
        if (lines.isEmpty()) {
            return;
        }

        final var y = -lines.size() * font.lineHeight / 2f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.55f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));
        poseStack.translate(0f, 0f, 1/16f);
        poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        for (int i = 0; i < lines.size(); i++) {
            final var text = lines.get(i);
            final var x = -font.width(text) / 2f;
            submitNodeCollector.submitText(poseStack, x, y + i * font.lineHeight, text, false, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT, TEXT_COLOR, 0, 0);
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
