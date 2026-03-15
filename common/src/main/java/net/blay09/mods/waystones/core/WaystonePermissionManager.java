package net.blay09.mods.waystones.core;

import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.config.rules.WaystoneRuleContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class WaystonePermissionManager {

    public static Optional<Component> mayEditWaystone(ServerPlayer player, Waystone waystone) {
        final var context = MutableShogiContext.of(player);
        WaystoneRuleContext.setEffectiveWaystone(context, waystone);
        final var editResult = WaystonesRules.mayEdit.get(context);
        if (editResult.right().isPresent()) {
            return editResult.right().map(Coercion.COMPONENT);
        } else if (!editResult.left().orElse(false)) {
            return Optional.of(Component.translatable("chat.waystones.not_allowed_to_edit"));
        }

        return Optional.empty();
    }

    public static boolean isEntityDeniedTeleports(Entity entity) {
        final var deniedEntities = WaystonesConfig.getActive().rules.entityDenyList;
        return deniedEntities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }
}
