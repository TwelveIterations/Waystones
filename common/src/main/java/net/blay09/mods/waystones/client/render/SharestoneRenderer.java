package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SharestoneRenderer implements BlockEntityRenderer<SharestoneBlockEntity, SharestoneRenderer.SharestoneRenderState> {

    public static class SharestoneRenderState extends BlockEntityRenderState {
        public boolean skip;
        public boolean glow;
        public int runeColor;
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float itemYaw;
        public float itemOffsetY;
    }

    private static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("waystone_overlays/sharestone_color"));

    private static ItemStack warpStoneItem;

    private final MaterialSet materials;
    private final ItemModelResolver itemModelResolver;
    private final SharestoneModel model;

    public SharestoneRenderer(BlockEntityRendererProvider.Context context) {
        materials = context.materials();
        itemModelResolver = context.itemModelResolver();
        model = new SharestoneModel(context.bakeLayer(ModRenderers.sharestoneModel));
    }

    @Override
    public SharestoneRenderState createRenderState() {
        return new SharestoneRenderState();
    }

    @Override
    public void extractRenderState(SharestoneBlockEntity blockEntity, SharestoneRenderState renderState, float delta, Vec3 vec, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, delta, vec, crumblingOverlay);
        if (renderState.blockState.getValue(SharestoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            renderState.skip = true;
            return;
        }

        final var level = blockEntity.getLevel();
        long gameTime = level != null ? level.getGameTime() : 0;

        renderState.glow = !WaystonesConfig.getActive().client.disableTextGlow;
        renderState.runeColor = ((SharestoneBlock) blockEntity.getBlockState().getBlock()).getColor().getTextureDiffuseColor();

        if (warpStoneItem == null) {
            warpStoneItem = new ItemStack(ModItems.warpStone);
            level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.UNBREAKING).ifPresent(it -> warpStoneItem.enchant(it, 1));
        }

        itemModelResolver.updateForTopItem(renderState.item, warpStoneItem, ItemDisplayContext.FIXED, level, null, (int) renderState.blockPos.asLong());
        renderState.itemYaw = gameTime / 2f % 360;
        renderState.itemOffsetY = (float) Math.sin(gameTime / 8f) * 0.025f;
    }

    @Override
    public void submit(SharestoneRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.skip) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.XN.rotationDegrees(180f));
        poseStack.translate(0f, -2f, 0f);
        float scale = 1.01f;
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.scale(scale, scale, scale);
        final var sprite = materials.get(MATERIAL);
        submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, MATERIAL.renderType(RenderType::entityCutout), renderState.glow ? LightTexture.FULL_BRIGHT : renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, renderState.runeColor, renderState.breakProgress);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5f, 1f + renderState.itemOffsetY, 0.5f);
        poseStack.mulPose(Axis.YN.rotationDegrees(renderState.itemYaw));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderState.item.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
