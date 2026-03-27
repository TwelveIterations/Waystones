package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class WaystoneRenderer implements BlockEntityRenderer<WaystoneBlockEntity, WaystoneRenderer.WaystoneRenderState> {

    public static class WaystoneRenderState extends BlockEntityRenderState {
        public boolean skip;
        public Direction facing = Direction.NORTH;
        public boolean glow;
        public boolean showRunes;
        public int runeColor;
    }

    private static final SpriteId MATERIAL = new SpriteId(TextureAtlas.LOCATION_BLOCKS,
            Identifier.withDefaultNamespace("waystone_overlays/waystone_active"));

    private final SpriteGetter materials;
    private final SharestoneModel model;

    public WaystoneRenderer(BlockEntityRendererProvider.Context context) {
        materials = context.sprites();
        model = new SharestoneModel(context.bakeLayer(ModRenderers.waystoneModel));
    }

    @Override
    public WaystoneRenderState createRenderState() {
        return new WaystoneRenderState();
    }

    @Override
    public void extractRenderState(WaystoneBlockEntity blockEntity, WaystoneRenderState renderState, float delta, Vec3 vec, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, delta, vec, crumblingOverlay);
        final var blockState = blockEntity.getBlockState();
        if (blockState.getValue(SharestoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            renderState.skip = true;
            return;
        }

        renderState.facing = blockState.getValue(WaystoneBlock.FACING);
        final var type = blockState.getBlock() instanceof WaystoneBlock waystoneBlock ? waystoneBlock.getType() : WaystoneTypes.ANDESITE;
        renderState.runeColor = type.runeColor();
        final var player = Minecraft.getInstance().player;
        boolean isActivated = player != null && PlayerWaystoneManager.isWaystoneActivated(player, blockEntity.getWaystone());
        renderState.showRunes = isActivated || blockState.getValue(WaystoneBlock.SEEN);
        if (!isActivated) {
            renderState.runeColor = 0xFF444444;
        } else {
            renderState.glow = !WaystonesConfig.getActive().client.disableTextGlow;
        }
    }

    @Override
    public void submit(WaystoneRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.skip) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(180f));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        if (renderState.showRunes) {
            poseStack.scale(1.05f, 1.05f, 1.05f);
            final var sprite = materials.get(MATERIAL);
            submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, MATERIAL.renderType(RenderTypes::entityCutout), renderState.glow ? LightCoordsUtil.FULL_BRIGHT : renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.runeColor, sprite, 0, renderState.breakProgress);
        }
        poseStack.popPose();
    }
}
