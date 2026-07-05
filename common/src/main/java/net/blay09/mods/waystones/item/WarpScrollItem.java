package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WarpScrollItem extends ScrollItemBase implements IResetUseOnDamage {

    public WarpScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return WaystonesConfig.getActive().general.scrollUseTime;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            final var hand = player.getUsedItemHand();
            Balm.getNetworking().openGui(player, new WaystoneSelectionListBuilder(player)
                    .withTargetsForItem(itemStack)
                    .withHand(hand)
                    .withPostTeleportHandler(context -> itemStack.consume(1, player))
                    .buildMenuProvider(ModMenus.warpScrollSelection.get(), Component.translatable("container.waystones.waystone_selection")));
        }
        return itemStack;
    }

}
