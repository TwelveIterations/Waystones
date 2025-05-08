package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SoftCooldownRequirement implements WarpRequirement {

    private ResourceLocation key;
    private int seconds;

    public SoftCooldownRequirement(ResourceLocation key, int seconds) {
        this.key = key;
        this.seconds = seconds;
    }

    @Override
    public boolean canAfford(Player player) {
        return true;
    }

    @Override
    public void consume(Player player) {
        if (seconds > 0) {
            PlayerWaystoneManager.setCooldownUntil(player, key, System.currentTimeMillis() + seconds * 1000L);
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }
    }

    @Override
    public void rollback(Player player) {
        PlayerWaystoneManager.setCooldownUntil(player, key, 0);
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        final var millisLeft = getCooldownMillisLeft(player);
        if (millisLeft > 0) {
            tooltip.add(Component.translatable("tooltip.waystones.cooldown_left", millisLeft / 1000).withStyle(ChatFormatting.GOLD));
        }
    }

    public long getCooldownMillisLeft(Player player) {
        return PlayerWaystoneManager.getCooldownMillisLeft(player, key);
    }

    @Override
    public boolean isEmpty() {
        return seconds <= 0;
    }

    public void setCooldown(ResourceLocation key, int seconds) {
        this.key = key;
        this.seconds = seconds;
    }

    public ResourceLocation getCooldownKey() {
        return key;
    }

    public int getCooldownSeconds() {
        return seconds;
    }
}
