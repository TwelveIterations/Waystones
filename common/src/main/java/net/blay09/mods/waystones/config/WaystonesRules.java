package net.blay09.mods.waystones.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.blay09.mods.balm.platform.event.callback.ConfigCallback;
import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.ShogiValue;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.effect.compose.AggregateEffect;
import net.blay09.mods.shogi.common.parse.ShogiRuleParser;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.rules.*;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystonesRules {

    public static final Logger logger = LoggerFactory.getLogger(WaystonesRules.class);

    private static ShogiEffect<?> cachedWarpRequirements;

    public static final ShogiValue<WaystoneTeleportContext, List<?>> warpRequirements = Shogi.maybe(id("warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST);
    public static final ShogiValue<WaystoneTeleportContext, List<?>> inventoryButtonWarpRequirements = Shogi.maybe(id("inventory_button_warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST).networked();
    public static final ShogiValue<Entity, Integer> warpStoneUseTime = Shogi.intValue(id("warp_stone_use_time"), _ -> 32);
    public static final ShogiValue<BlockEntity, Integer> warpPlateUseTime = Shogi.intValue(id("warp_plate_use_time"), _ -> 15);
    public static final ShogiValue<Entity, Integer> scrollUseTime = Shogi.intValue(id("scroll_use_time"), _ -> 32);

    public static final ShogiScope scope = Shogi.scope(id("rules"), it -> {
        it.setDefaultNamespaces(List.of("waystones", "shogi"));

        it.registerEffect(Source.IDENTIFIER, Source.mapCodec(it), List.of("effect"));
        it.registerEffect(Target.IDENTIFIER, Target.mapCodec(it), List.of("effect"));

        it.registerEffect(NameEquals.IDENTIFIER, NameEquals.MAP_CODEC, List.of("name"));
        it.registerEffect(NameContains.IDENTIFIER, NameContains.MAP_CODEC, List.of("name"));

        it.registerEffect(IsDimension.IDENTIFIER, IsDimension.MAP_CODEC, List.of("dimension"));
        it.registerEffect(InvolvesDimension.IDENTIFIER, InvolvesDimension.MAP_CODEC, List.of("dimension"));

        it.registerEffect(IsWithinDistance.IDENTIFIER, IsWithinDistance.MAP_CODEC, List.of("distance"));

        it.registerSimpleEffect(id("is_interdimensional"), WaystoneRuleContext::isDimensionalTeleport);

        it.registerSimpleEffect(id("is_warp_plate"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getWaystoneKind().equals(WaystoneKinds.WARP_PLATE))
                .isPresent());

        it.registerSimpleEffect(id("is_portstone"), context
                -> WaystoneRuleContext.getFlags(context).contains(TeleportFlags.PORTSTONE));

        it.registerSimpleEffect(id("is_waystone"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE))
                .isPresent());

        it.registerSimpleEffect(id("is_sharestone"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> WaystoneKinds.isSharestone(waystone.getWaystoneKind()))
                .isPresent());

        it.registerSimpleEffect(id("is_inventory_button"), context
                -> WaystoneRuleContext.getFlags(context).contains(TeleportFlags.INVENTORY_BUTTON));

        it.registerSimpleEffect(id("is_scroll"), context
                -> context.itemStack().is(ModItemTags.SCROLLS));

        it.registerSimpleEffect(id("is_bound_scroll"), context
                -> context.itemStack().is(ModItemTags.BOUND_SCROLLS));

        it.registerSimpleEffect(id("is_return_scroll"), context
                -> context.itemStack().is(ModItemTags.RETURN_SCROLLS));

        it.registerSimpleEffect(id("is_warp_scroll"), context
                -> context.itemStack().is(ModItemTags.WARP_SCROLLS));

        it.registerSimpleEffect(id("is_warp_stone"), context
                -> context.itemStack().is(ModItemTags.WARP_STONES));

        it.registerSimpleEffect(id("is_global"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.GLOBAL)
                .isPresent());

        it.registerSimpleEffect(id("is_owner"), context
                -> context.entity() instanceof Player player
                && WaystoneRuleContext.getEffectiveWaystone(context)
                .flatMap(waystone -> waystone.getOwnerUid()
                        .filter(ownerUid -> ownerUid.equals(player.getGameProfile().id())))
                .isPresent());

        it.registerSimpleEffect(id("has_owner"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .map(waystone -> waystone.getOwnerUid().isPresent())
                .orElse(false));

        it.registerSimpleEffect(id("is_with_pets"), context
                -> context.entity() instanceof LivingEntity livingEntity
                && !WaystoneTeleportManager.findPets(livingEntity).isEmpty());

        it.registerSimpleEffect(id("is_with_passengers"), context
                -> !WaystoneTeleportManager.findPassengers(context.entity()).isEmpty());

        it.registerSimpleEffect(id("is_with_leashed"), context
                -> !WaystoneTeleportManager.findLeashedAnimals(context.entity()).isEmpty());

        for (final var sharestoneType : WaystoneKinds.SHARESTONES) {
            final Identifier sharestoneIdentifier = id("is_" + sharestoneType.getPath());
            it.registerSimpleEffect(sharestoneIdentifier, context
                    -> WaystoneRuleContext.getEffectiveWaystone(context)
                    .filter(waystone -> sharestoneType.equals(waystone.getWaystoneKind()))
                    .isPresent());
        }
    });

    public static void initialize() {
        ConfigCallback.Reloaded.EVENT.register(schema -> {
            if (schema.identifier().equals(id("common"))) {
                cachedWarpRequirements = null;
            }
        });
    }

    private static ShogiEffect<?> getOrBuildWarpRequirementsEffect(WaystoneTeleportContext context) {
        if (cachedWarpRequirements == null) {
            final var level = context.requireLevel();
            final var registryOps = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
            final List<ShogiEffect<?>> rules = WaystonesConfig.getActive().teleports.rules.stream()
                    .map(it -> ShogiRuleParser.parse(scope, registryOps, it))
                    .filter(shogiEffectDataResult -> {
                        shogiEffectDataResult.error().ifPresent(error -> logger.error("Invalid warp requirements rule {}", error));
                        return shogiEffectDataResult.isSuccess();
                    })
                    .map(it -> it.result().orElseThrow())
                    .collect(Collectors.toList());
            cachedWarpRequirements = AggregateEffect.withAutoApplied(scope, registryOps, rules);
        }
        return cachedWarpRequirements;
    }

    private static Either<?, ?> resolveWarpRequirements(WaystoneTeleportContext context) {
        return getOrBuildWarpRequirementsEffect(context).apply(context);
    }
}
