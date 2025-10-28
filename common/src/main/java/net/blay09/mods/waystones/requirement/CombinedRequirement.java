package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

public class CombinedRequirement implements WarpRequirement {

    private final Collection<WarpRequirement> requirements;

    public CombinedRequirement(Collection<WarpRequirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean canAfford(Player player) {
        return requirements.stream().allMatch(requirement -> requirement.canAfford(player));
    }

    @Override
    public void consume(WaystoneTeleportContext context, Player player) {
        requirements.forEach(requirement -> requirement.consume(context, player));
    }

    @Override
    public void rollback(WaystoneTeleportContext context, Player player) {
        requirements.forEach(requirement -> requirement.rollback(context, player));
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        requirements.forEach(requirement -> requirement.appendHoverText(player, tooltip));
    }

    @Override
    public boolean isEmpty() {
        return requirements.stream().allMatch(WarpRequirement::isEmpty);
    }

    public Collection<WarpRequirement> getRequirements() {
        return requirements;
    }
}
