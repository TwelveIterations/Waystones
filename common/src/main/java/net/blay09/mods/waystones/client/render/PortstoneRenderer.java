package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.PortstoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PortstoneRenderer implements BlockEntityRenderer<PortstoneBlockEntity, PortstoneRenderer.PortstoneRenderState> {

    public static class PortstoneRenderState extends BlockEntityRenderState {
        public boolean skip;
        public Direction facing = Direction.NORTH;
        public boolean glow;
        public int runeColor;
        public final ItemStackRenderState item = new ItemStackRenderState();
    }

    private static final SpriteId MATERIAL = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("waystone_overlays/portstone"));
    private static ItemStack warpStoneItem;

    private final SpriteGetter materials;
    private final ItemModelResolver itemModelResolver;
    private final PortstoneModel model;

    public PortstoneRenderer(BlockEntityRendererProvider.Context context) {
        materials = context.sprites();
        model = new PortstoneModel(context.bakeLayer(ModRenderers.portstoneModel));
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PortstoneRenderState createRenderState() {
        return new PortstoneRenderState();
    }

    @Override
    public void extractRenderState(PortstoneBlockEntity blockEntity, PortstoneRenderState renderState, float delta, Vec3 vec, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, delta, vec, crumblingOverlay);
        final var blockState = blockEntity.getBlockState();
        if (blockState.getValue(SharestoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            renderState.skip = true;
            return;
        }

        renderState.facing = blockState.getValue(PortstoneBlock.FACING);
        renderState.glow = !WaystonesConfig.getActive().client.disableTextGlow;
        renderState.runeColor = ((PortstoneBlock) blockEntity.getBlockState().getBlock()).getColor().getTextureDiffuseColor();

        final var level = blockEntity.getLevel();
        if (warpStoneItem == null) {
            warpStoneItem = ModItems.warpStone.createStack();
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
        poseStack.mulPose(Axis.YN.rotationDegrees(renderState.facing.toYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(180f));
        poseStack.translate(0f, -2f, 0f);
        float scale = 1.01f;
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.scale(scale, scale, scale);
        final var sprite = materials.get(MATERIAL);
        submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, MATERIAL.renderType(RenderTypes::entityCutout), renderState.glow ? LightCoordsUtil.FULL_BRIGHT : renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.runeColor, sprite, 0, renderState.breakProgress);
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
