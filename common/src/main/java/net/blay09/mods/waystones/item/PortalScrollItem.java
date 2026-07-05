package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.WarpPortalManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PortalScrollItem extends ScrollItemBase implements IResetUseOnDamage {

    public PortalScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return WaystonesConfig.getActive().general.scrollUseTime;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (!WarpPortalManager.hasSpaceForPortal(player)) {
                player.sendSystemMessage(Component.translatable("chat.waystones.warp_portal_no_space").withStyle(ChatFormatting.DARK_RED));
                return itemStack;
            }

            Balm.getNetworking().openGui(player, new WaystoneSelectionListBuilder(player)
                    .withTargetsForPlayer()
                    .withWarpItem(itemStack)
                    .withPostTeleportHandler(context -> {
                        if (WarpPortalManager.spawnPortal(player, context.getTargetWaystone())) {
                            itemStack.consume(1, player);
                        } else {
                            player.sendSystemMessage(Component.translatable("chat.waystones.warp_portal_no_space").withStyle(ChatFormatting.DARK_RED));
                        }
                    })
                    .buildMenuProvider(ModMenus.portalScrollSelection.get(), Component.translatable("container.waystones.waystone_selection")));
        }

        return itemStack;
    }
}
