package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.core.TwinboundFeatherTargets;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TwinboundFeatherRequirement implements WarpRequirement {
    public static final TwinboundFeatherRequirement INSTANCE = new TwinboundFeatherRequirement();

    @Override
    public boolean canAfford(Player player) {
        return true;
    }

    @Override
    public void consume(Player player) {
    }

    @Override
    public void rollback(Player player) {
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        tooltip.add(Component.translatable("gui.waystones.waystone_selection.twinbound_feather_requirement",
                new ItemStack(ModItems.twinboundFeather).getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
