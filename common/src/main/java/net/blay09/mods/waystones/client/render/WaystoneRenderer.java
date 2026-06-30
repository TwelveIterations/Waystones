package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.waystones.api.WaystoneStyles;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.client.ModModels;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Objects;

public class WaystoneRenderer implements BlockEntityRenderer<WaystoneBlockEntity> {

    public WaystoneRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WaystoneBlockEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tileEntity.getBlockState();
        if (state.getValue(WaystoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(Objects.requireNonNull(player), tileEntity.getWaystone());
        if (!isActivated) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(WaystoneBlock.FACING).toYRot()));
        poseStack.translate(-0.5f, 0f, -0.5f);
        final var style = WaystoneStyles.getStyle(state.getBlock());
        RuneRenderUtil.render(ModModels.waystoneRunes.get(),
                poseStack,
                buffer,
                style != null ? style.getRuneColor() : 0xFFFFFFFF,
                combinedLightIn,
                !WaystonesConfig.getActive().client.disableTextGlow);
        poseStack.popPose();
    }
}
