package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface WarpRequirement {
    boolean canAfford(Player player);

    /**
     * @deprecated Use the overload with an additional context parameter.
     */
    @Deprecated
    default void consume(Player player) {}

    default void consume(WaystoneTeleportContext context, Player player) {
        consume(player);
    }

    /**
     * @deprecated Use the overload with an additional context parameter.
     */
    @Deprecated
    default void rollback(Player player) {}

    default void rollback(WaystoneTeleportContext context, Player player) {
        rollback(player);
    }

    void appendHoverText(Player player, List<Component> tooltip);

    default boolean isEmpty() {
        return false;
    }
}
