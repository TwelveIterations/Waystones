package net.blay09.mods.waystones.config;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.ShogiValue;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.effect.compose.AggregateEffect;
import net.blay09.mods.shogi.common.parse.ShogiRuleParser;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.rules.*;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystonesRules {

    public static final Logger logger = LoggerFactory.getLogger(WaystonesRules.class);

    public static final ShogiValue<WaystoneTeleportContext, List<?>> warpRequirements = Shogi.maybe(id("warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST);

    public static final ShogiValue<WaystoneTeleportContext, List<?>> inventoryButtonWarpRequirements = Shogi.maybe(id("inventory_button_warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST).networked();

    public static final ShogiScope scope = Shogi.scope(id("default"), it -> {
        it.setDefaultNamespaces(List.of("waystones", "shogi"));

        it.registerEffect(Source.IDENTIFIER, Source.mapCodec(it), List.of("effect"));
        it.registerEffect(Target.IDENTIFIER, Target.mapCodec(it), List.of("effect"));

        it.registerEffect(NameEquals.IDENTIFIER, NameEquals.MAP_CODEC, List.of("name"));
        it.registerEffect(NameContains.IDENTIFIER, NameContains.MAP_CODEC, List.of("name"));

        it.registerEffect(IsDimension.IDENTIFIER, IsDimension.MAP_CODEC, List.of("dimension"));
        it.registerEffect(InvolvesDimension.IDENTIFIER, InvolvesDimension.MAP_CODEC, List.of("dimension"));

        it.registerEffect(IsWithinDistance.IDENTIFIER, IsWithinDistance.MAP_CODEC, List.of("distance"));

        it.registerSimpleEffect(id("is_interdimensional"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.isDimensionalTeleport());

        it.registerSimpleEffect(id("is_warp_plate"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && WaystoneRuleContext.getEffectiveWaystone(context, waystoneContext)
                .filter(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE))
                .isPresent());

        it.registerSimpleEffect(id("is_portstone"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getFlags().contains(TeleportFlags.PORTSTONE));

        it.registerSimpleEffect(id("is_waystone"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && WaystoneRuleContext.getEffectiveWaystone(context, waystoneContext)
                .filter(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE))
                .isPresent());

        it.registerSimpleEffect(id("is_sharestone"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && WaystoneRuleContext.getEffectiveWaystone(context, waystoneContext)
                .filter(waystone -> WaystoneTypes.isSharestone(waystone.getWaystoneType()))
                .isPresent());

        it.registerSimpleEffect(id("is_inventory_button"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getFlags().contains(TeleportFlags.INVENTORY_BUTTON));

        it.registerSimpleEffect(id("is_scroll"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getWarpItem().is(ModItemTags.SCROLLS));

        it.registerSimpleEffect(id("is_bound_scroll"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getWarpItem().is(ModItemTags.BOUND_SCROLLS));

        it.registerSimpleEffect(id("is_return_scroll"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getWarpItem().is(ModItemTags.RETURN_SCROLLS));

        it.registerSimpleEffect(id("is_warp_scroll"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getWarpItem().is(ModItemTags.WARP_SCROLLS));

        it.registerSimpleEffect(id("is_warp_stone"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getWarpItem().is(ModItemTags.WARP_STONES));

        it.registerSimpleEffect(id("is_global"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && WaystoneRuleContext.getEffectiveWaystone(context, waystoneContext)
                .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.GLOBAL)
                .isPresent());

        it.registerSimpleEffect(id("is_with_pets"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && waystoneContext.getEntity() instanceof LivingEntity livingEntity
                && !WaystoneTeleportManager.findPets(livingEntity).isEmpty());

        it.registerSimpleEffect(id("is_with_passengers"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && !WaystoneTeleportManager.findPassengers(waystoneContext.getEntity()).isEmpty());

        it.registerSimpleEffect(id("is_with_leashed"), context
                -> context instanceof WaystoneTeleportContext waystoneContext
                && !WaystoneTeleportManager.findLeashedAnimals(waystoneContext.getEntity()).isEmpty());

        for (final var sharestoneType : WaystoneTypes.SHARESTONES) {
            final Identifier sharestoneIdentifier = id("is_" + sharestoneType.getPath());
            it.registerSimpleEffect(sharestoneIdentifier, context
                    -> context instanceof WaystoneTeleportContext waystoneContext
                    && WaystoneRuleContext.getEffectiveWaystone(context, waystoneContext)
                    .filter(waystone -> sharestoneType.equals(waystone.getWaystoneType()))
                    .isPresent());
        }
    });

    private static Either<?, ?> resolveWarpRequirements(WaystoneTeleportContext context) {
        final List<ShogiEffect<?>> rules = WaystonesConfig.getActive().teleports.warpRequirements.stream()
                .map(it -> ShogiRuleParser.parse(scope, it))
                .filter(shogiEffectDataResult -> {
                    shogiEffectDataResult.error().ifPresent(error -> logger.error("Invalid warp requirements rule {}", error));
                    return shogiEffectDataResult.isSuccess();
                })
                .map(it -> it.result().orElseThrow())
                .collect(Collectors.toList());
        final var aggregate = AggregateEffect.withAutoApplied(scope, rules);
        return aggregate.apply(context);
    }
}
