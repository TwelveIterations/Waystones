package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.entity.PortstoneBlockEntity;
import net.blay09.mods.waystones.client.ModModels;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class PortstoneRenderer implements BlockEntityRenderer<PortstoneBlockEntity> {
    private static ItemStack warpStoneItem;

    public PortstoneRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PortstoneBlockEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        final var level = tileEntity.getLevel();
        final var state = tileEntity.getBlockState();
        if (level == null || state.getValue(PortstoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        final var facing = state.getValue(PortstoneBlock.FACING);

        final var color = ((PortstoneBlock) state.getBlock()).getColor();
        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        poseStack.translate(-0.5f, 0f, -0.5f);
        RuneRenderUtil.render(ModModels.portstoneRunes.get(),
                poseStack,
                buffer,
                color.getTextureDiffuseColor(),
                combinedLightIn,
                !WaystonesConfig.getActive().client.disableTextGlow);
        poseStack.popPose();

        if (warpStoneItem == null) {
            warpStoneItem = new ItemStack(ModItems.warpStone);
            level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.UNBREAKING).ifPresent(it -> warpStoneItem.enchant(it, 1));
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 1f, 0.5f);
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
        poseStack.translate(0f, 0f, 0.15f);
        poseStack.mulPose(Axis.XN.rotationDegrees(25f));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(0.03125f, 0f, 0f);
        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(warpStoneItem, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, level, 0);
        poseStack.popPose();
    }
}
