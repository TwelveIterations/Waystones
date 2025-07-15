package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DismountRequirement implements WarpRequirement {

    @Override
    public boolean canAfford(Player player) {
        return true;
    }

    @Override
    public void consume(Player player) {
        player.stopRiding();
    }

    @Override
    public void rollback(Player player) {
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
    }

}
