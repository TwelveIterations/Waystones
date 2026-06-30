package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.api.WarpStoneTypes;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.PortstoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix4f;

public class PortstoneRenderer implements BlockEntityRenderer<PortstoneBlockEntity, PortstoneRenderer.PortstoneRenderState> {

    public static class PortstoneRenderState extends BlockEntityRenderState {
        public final BlockModelRenderState runes = new BlockModelRenderState();
        public boolean skip;
        public Direction facing = Direction.NORTH;
        public boolean glow;
        public int runeColor;
        public final ItemStackRenderState item = new ItemStackRenderState();
    }

    private static @Nullable ItemStack warpStoneItem;

    private final ItemModelResolver itemModelResolver;

    public PortstoneRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PortstoneRenderState createRenderState() {
        return new PortstoneRenderState();
    }

    @Override
    public void extractRenderState(PortstoneBlockEntity blockEntity, PortstoneRenderState renderState, float delta, Vec3 vec, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, delta, vec, crumblingOverlay);
        renderState.skip = false;
        renderState.runes.clear();

        final var blockState = blockEntity.getBlockState();
        if (blockState.getValue(SharestoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            renderState.skip = true;
            return;
        }

        renderState.facing = blockState.getValue(PortstoneBlock.FACING);
        renderState.glow = !WaystonesConfig.getActive().client.disableTextGlow;
        renderState.runeColor = ((PortstoneBlock) blockEntity.getBlockState().getBlock()).getType().textureDiffuseColor();
        final var model = ModRenderers.portstoneRunesModel.asBlockStateModel();
        final var modelParts = renderState.runes.setupModel(new Matrix4f(), false);
        final var random = renderState.runes.scratchRandomSource(blockState.getSeed(blockEntity.getBlockPos()));
        model.collectParts(random, modelParts);
        renderState.runes.tintLayers().add(renderState.runeColor);

        final var level = blockEntity.getLevel();
        if (warpStoneItem == null) {
            warpStoneItem = ModItems.warpStones.get(WarpStoneTypes.UNSCOPED).createStack();
            level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.UNBREAKING).ifPresent(it -> warpStoneItem.enchant(it, 1));
        }

        itemModelResolver.updateForTopItem(renderState.item, warpStoneItem, ItemDisplayContext.FIXED, level, null, (int) renderState.blockPos.asLong());
    }

    @Override
    public void submit(PortstoneRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.skip) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));
        poseStack.translate(-0.5f, 0f, -0.5f);
        renderState.runes.submit(poseStack, submitNodeCollector, renderState.glow ? LightCoordsUtil.FULL_BRIGHT : renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5f, 1f, 0.5f);
        poseStack.mulPose(Axis.YN.rotationDegrees(renderState.facing.toYRot()));
        poseStack.translate(0f, 0f, 0.15f);
        poseStack.mulPose(Axis.XN.rotationDegrees(25f));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(0.03125f, 0f, 0f);
        renderState.item.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
