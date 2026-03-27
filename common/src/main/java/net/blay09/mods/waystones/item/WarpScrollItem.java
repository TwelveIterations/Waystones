package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;

public class WarpScrollItem extends ScrollItemBase implements IResetUseOnDamage {

    public WarpScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return WaystonesRules.scrollUseTime.getOrDefault(entity);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            final var waystones = new ArrayList<>(PlayerWaystoneManager.getTargetsForItem(player, itemStack));
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            Balm.networking().openMenu(player, new BalmMenuProvider<ModMenus.ItemInitiatedWaystoneMenuData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.value(), null, windowId, waystones, Collections.emptyMap(), Collections.emptySet())
                            .withWarpItem(itemStack)
                            .setPostTeleportHandler(context -> itemStack.consume(1, inventory.player));
                }


                @Override
                public ModMenus.ItemInitiatedWaystoneMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    final var warpRequirements = WaystoneSelectionMenu.buildWarpRequirements(serverPlayer, null, waystones, Collections.emptySet(), itemStack, InteractionHand.MAIN_HAND);
                    return new ModMenus.ItemInitiatedWaystoneMenuData(waystones, itemStack, warpRequirements);
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
