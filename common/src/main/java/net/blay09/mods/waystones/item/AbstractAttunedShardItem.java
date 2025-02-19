package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.menu.WarpPlateContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractAttunedShardItem extends Item implements IAttunementItem {

    public AbstractAttunedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IWaystone waystoneAttunedTo = getWaystoneAttunedTo(null, itemStack);
        return waystoneAttunedTo != null && waystoneAttunedTo.isValid();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        IWaystone attunedWarpPlate = getWaystoneAttunedTo(null, stack);
        if (attunedWarpPlate == null || !attunedWarpPlate.isValid()) {
            var textComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_lost");
            textComponent.withStyle(ChatFormatting.GRAY);
            list.add(textComponent);
            return;
        }

        list.add(WarpPlateBlock.getGalacticName(attunedWarpPlate));

        Player player = Balm.getProxy().getClientPlayer();
        if (player != null && player.containerMenu instanceof WarpPlateContainer) {
            IWaystone currentWarpPlate = ((WarpPlateContainer) player.containerMenu).getWaystone();
            if (attunedWarpPlate.getWaystoneUid().equals(currentWarpPlate.getWaystoneUid())) {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.move_to_other_warp_plate"));
            } else {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
            }
        } else {
            list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
        }
    }

    @Nullable
    @Override
    public IWaystone getWaystoneAttunedTo(MinecraftServer server, ItemStack itemStack) {
        CompoundTag compound = itemStack.getTag();
        if (compound != null && compound.contains("AttunedToWaystone", Tag.TAG_INT_ARRAY)) {
            return new WaystoneProxy(server, NbtUtils.loadUUID(Objects.requireNonNull(compound.get("AttunedToWaystone"))));
        }

        return null;
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable IWaystone waystone) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            itemStack.setTag(tagCompound);
        }

        if (waystone != null) {
            tagCompound.put("AttunedToWaystone", NbtUtils.createUUID(waystone.getWaystoneUid()));
        } else {
            tagCompound.remove("AttunedToWaystone");
        }
    }
}
