package net.blay09.mods.waystones.compat;

import net.blay09.mods.unbreakables.api.UnbreakablesAPI;
import net.blay09.mods.unbreakables.rules.parameters.NoParameter;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.WaystonesAPI;

public class UnbreakablesIntegration {
    public UnbreakablesIntegration() {
        UnbreakablesAPI.registerCondition("is_waystone_owner", NoParameter.class, (context, params) -> {
            final var server = context.getPlayer().getServer();
            if (server != null) {
                final var waystone = WaystonesAPI.getWaystoneAt(server, context.getBlockGetter(), context.getPos());
                return waystone.map(it -> it.isOwner(context.getPlayer()))
                        .orElse(true); // waystone not found -> allow breaking by default
            }
            return true; // We default to true since this can only be verified on the server.
        });

        UnbreakablesAPI.registerCondition("is_waystone_global", NoParameter.class, (context, params) -> {
            final var server = context.getPlayer().getServer();
            if (server != null) {
                final var waystone = WaystonesAPI.getWaystoneAt(server, context.getBlockGetter(), context.getPos());
                return waystone.map(it -> it.getVisibility() == WaystoneVisibility.GLOBAL)
                        .orElse(true); // waystone not found -> allow breaking by default
            }
            return true; // We default to true since this can only be verified on the server.
        });
    }
}
