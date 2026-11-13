package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.WaystoneReferenceComponent;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

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
        final var attunement = stack.get(ModComponents.warpPlateAttunement.value());
        if (attunement != null) {
            list.accept(WarpPlateBlock.getGalacticName(attunement.waystoneId()));
        }
        final var legacyAttunement = stack.get(ModComponents.attunement.value());
        if (legacyAttunement != null) {
            list.accept(WarpPlateBlock.getGalacticName(legacyAttunement));
        }
        list.accept(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        final var absolutePos = itemStack.get(ModComponents.absolutePosAttunement.value());
        if (absolutePos != null) {
            final var level = server.getLevel(absolutePos.dimension());
            if (level == null) {
                return Optional.empty();
            }

            if (level.isLoaded(absolutePos.pos())) {
                return level.getBlockEntity(absolutePos.pos()) instanceof WaystoneBlockEntityBase target
                        ? Optional.of(target.getOrLoadWaystone()).filter(Waystone::isValid) : Optional.empty();
            } else {
                return SavedDataWaystonesStore.get(server).getWaystoneAt(absolutePos);
            }
        }

        final var attunement = itemStack.get(ModComponents.warpPlateAttunement.value());
        if (attunement != null) {
            return Optional.of(new WaystoneProxy(server, attunement.waystoneId()));
        }

        final var legacyAttunement = itemStack.get(ModComponents.attunement.value());
        if (legacyAttunement != null) {
            return Optional.of(new WaystoneProxy(server, legacyAttunement));
        }

        return Optional.empty();
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable Waystone waystone) {
        itemStack.remove(ModComponents.relativePosAttunement.value());
        itemStack.remove(ModComponents.absolutePosAttunement.value());
        if (waystone != null) {
            itemStack.set(ModComponents.warpPlateAttunement.value(), new WaystoneReferenceComponent(waystone.getWaystoneUid(), waystone.getName()));
        } else {
            itemStack.remove(ModComponents.warpPlateAttunement.value());
        }
    }
}
