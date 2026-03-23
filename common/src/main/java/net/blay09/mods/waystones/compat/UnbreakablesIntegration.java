package net.blay09.mods.waystones.compat;

import net.blay09.mods.unbreakables.api.UnbreakablesAPI;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.rules.WaystoneRuleContext;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public class UnbreakablesIntegration {
    public UnbreakablesIntegration() {
        final var scope = UnbreakablesAPI.shogiScope();
        scope.registerSimpleEffect(id("is_owner"), context
                -> context.entity() instanceof Player player
                && WaystoneRuleContext.getEffectiveWaystone(context)
                .flatMap(waystone -> waystone.getOwnerUid()
                        .filter(ownerUid -> ownerUid.equals(player.getGameProfile().id())))
                .isPresent());

        scope.registerSimpleEffect(id("is_global"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.GLOBAL)
                .isPresent());
    }
}
