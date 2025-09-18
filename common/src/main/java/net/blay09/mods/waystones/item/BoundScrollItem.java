package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.api.trait.IFOVOnUse;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.component.BoundScrollComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class BoundScrollItem extends ScrollItemBase implements IResetUseOnDamage, IFOVOnUse, IAttunementItem {

    public BoundScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return WaystonesConfig.getActive().general.scrollUseTime;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            final var boundTo = getWaystoneAttunedTo(player.level().getServer(), player, stack);
            boundTo.ifPresent(targetWaystone -> WaystonesAPI.createDefaultTeleportContext(player, targetWaystone, it -> it.setWarpItem(stack))
                    .mapLeft(WaystonesAPI::tryTeleport)
                    .ifLeft(it -> stack.consume(1, player))
            );
        }

        return stack;
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        final var boundScroll = itemStack.get(ModComponents.boundScroll.get());
        if (boundScroll != null) {
            return Optional.of(new WaystoneProxy(server, boundScroll.waystoneId()));
        }

        final var legacyAttunement = itemStack.get(ModComponents.attunement.get());
        if (legacyAttunement != null) {
            return Optional.of(new WaystoneProxy(server, legacyAttunement));
        }

        return Optional.empty();
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable Waystone waystone) {
        if (waystone != null) {
            itemStack.set(ModComponents.boundScroll.get(), new BoundScrollComponent(waystone.getWaystoneUid(), waystone.getName()));
        } else {
            itemStack.remove(ModComponents.boundScroll.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        itemStack.addToTooltip(ModComponents.boundScroll.get(), context, display, list, flag);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
