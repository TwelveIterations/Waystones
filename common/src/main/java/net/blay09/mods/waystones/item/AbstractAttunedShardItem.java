package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;


public abstract class AbstractAttunedShardItem extends ShardItem implements IAttunementItem {

    public AbstractAttunedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        final var attunedWaystoneUid = stack.get(ModComponents.attunement.get());
        list.accept(WarpPlateBlock.getGalacticName(attunedWaystoneUid));
        list.accept(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        return Optional.ofNullable(itemStack.get(ModComponents.attunement.get())).map(attunement -> new WaystoneProxy(server, attunement));
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable Waystone waystone) {
        if (waystone != null) {
            itemStack.set(ModComponents.attunement.get(), waystone.getWaystoneUid());
        } else {
            itemStack.remove(ModComponents.attunement.get());
        }
    }
}
