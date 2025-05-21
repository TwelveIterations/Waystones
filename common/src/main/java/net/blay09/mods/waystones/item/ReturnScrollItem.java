package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.ReturnScrollComponent;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Optional;
import java.util.function.Consumer;

public class ReturnScrollItem extends BoundScrollItem {

    public ReturnScrollItem(Properties properties) {
        super(properties.component(ModComponents.returnScroll.get(), ReturnScrollComponent.INSTANCE));
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        return PlayerWaystoneManager.getNearestWaystone(player);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        itemStack.addToTooltip(ModComponents.returnScroll.get(), context, display, list, flag);
    }
}
