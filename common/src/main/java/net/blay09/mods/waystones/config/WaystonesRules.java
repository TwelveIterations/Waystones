package net.blay09.mods.waystones.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.ShogiValue;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.effect.compose.AggregateEffect;
import net.blay09.mods.shogi.common.parse.ShogiRuleParser;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystonesRules {
    public static final ShogiScope scope = Shogi.scope(id("default"), it -> {
        it.setDefaultNamespaces(List.of("waystones", "shogi"));
    });

    public static final ShogiValue<WaystoneTeleportContext, List<?>> warpRequirements = Shogi.maybe(id("warp_requirements"), WaystonesRules::apply).coerce(Coercion.LIST);

    public static final ShogiValue<WaystoneTeleportContext, List<?>> inventoryButtonWarpRequirements = Shogi.maybe(id("inventory_button_warp_requirements"), WaystonesRules::apply).coerce(Coercion.LIST).networked();

    private static Either<?, ?> apply(WaystoneTeleportContext context) {
        final List<ShogiEffect<?>> rules = WaystonesConfig.getActive().teleports.warpRequirements.stream()
                .map(it -> ShogiRuleParser.parse(scope, it))
                .filter(DataResult::isSuccess)
                .map(it -> it.result().orElseThrow())
                .collect(Collectors.toList());
        final var aggregate = AggregateEffect.withAutoApplied(scope, rules);
        return aggregate.apply(context);
    }
}
