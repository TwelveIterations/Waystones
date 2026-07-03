package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpPortalManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;

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

            final var waystones = PlayerWaystoneManager.getPlayerDecoratedWaystones(player, PlayerWaystoneManager.getTargetsForPlayer(player));
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            Balm.getNetworking().openGui(player, new BalmMenuProvider<ModMenus.ItemInitiatedWaystoneMenuData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return new WaystoneSelectionMenu(ModMenus.portalScrollSelection.get(), null, windowId, waystones, Collections.emptySet())
                            .withWarpItem(itemStack)
                            .setPostTeleportHandler(context -> {
                                if (WarpPortalManager.spawnPortal((ServerPlayer) inventory.player, context.getTargetWaystone())) {
                                    itemStack.consume(1, inventory.player);
                                } else {
                                    inventory.player.sendSystemMessage(Component.translatable("chat.waystones.warp_portal_no_space").withStyle(ChatFormatting.DARK_RED));
                                }
                            });
                }

                @Override
                public ModMenus.ItemInitiatedWaystoneMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    return new ModMenus.ItemInitiatedWaystoneMenuData(waystones, itemStack);
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, ModMenus.ItemInitiatedWaystoneMenuData> getScreenStreamCodec() {
                    return ModMenus.ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                }
            });
        }

        return itemStack;
    }
}
