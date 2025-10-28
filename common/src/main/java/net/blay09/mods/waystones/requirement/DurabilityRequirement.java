package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DurabilityRequirement implements WarpRequirement {
    private int damage;

    public DurabilityRequirement(int damage) {
        this.damage = Math.max(0, damage);
    }

    @Override
    public boolean canAfford(Player player) {
        return true;
    }

    @Override
    public void consume(WaystoneTeleportContext context, Player player) {
        final var itemStack = context.getWarpItem();
        if (!itemStack.isEmpty()) {
            itemStack.hurtAndBreak(damage, player, context.getWarpHand());
        }
    }

    @Override
    public void rollback(WaystoneTeleportContext context, Player player) {
        final var itemStack = context.getWarpItem();
        if (itemStack.isEmpty()) {
            itemStack.setDamageValue(itemStack.getDamageValue() - damage);
        }
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        if (damage > 0) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.durability_requirement", damage).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    @Override
    public boolean isEmpty() {
        return damage <= 0;
    }

    public void setDamage(int value) {
        this.damage = value;
    }

    public int getDamage() {
        return damage;
    }
}
