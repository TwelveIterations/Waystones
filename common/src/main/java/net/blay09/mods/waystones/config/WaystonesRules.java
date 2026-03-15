package net.blay09.mods.waystones.config;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.platform.event.callback.ConfigCallback;
import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.ShogiValue;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.CachedShogiRule;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.rules.*;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystonesRules {

    public static final ShogiValue<WaystoneTeleportContext, List<?>> warpRequirements = Shogi.maybe(id("warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST);
    public static final ShogiValue<WaystoneTeleportContext, List<?>> inventoryButtonWarpRequirements = Shogi.maybe(id("inventory_button_warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST).networked();
    public static final ShogiValue<Entity, Integer> warpStoneUseTime = Shogi.intValue(id("warp_stone_use_time"), _ -> 32);
    public static final ShogiValue<BlockEntity, Integer> warpPlateUseTime = Shogi.intValue(id("warp_plate_use_time"), _ -> 15);
    public static final ShogiValue<Entity, Integer> scrollUseTime = Shogi.intValue(id("scroll_use_time"), _ -> 32);

    public static final ShogiValue<ShogiContext, Boolean> mayManageGlobalWaystones = Shogi.maybe(id("may_manage_global_waystones"), (ShogiContext context) -> {
        final var player = context.requirePlayer();
        final var config = WaystonesConfig.getActive();
        if (config.rules.allowEveryoneToManageGlobalWaystones) {
            return Either.left(true);
        }
        if (config.rules.defaultVisibility == WaystonesConfig.DefaultWaystoneVisibility.GLOBAL) {
            return Either.left(true);
        }
        if (!player.isCreative()) {
            return Either.right(Component.translatable("chat.waystones.only_creative_can_edit"));
        }
        return Either.left(true);
    }).coerce(Coercion.BOOLEAN);

    public static final ShogiValue<ShogiContext, Boolean> mayEdit = Shogi.maybe(id("may_edit"), (ShogiContext context) -> {
        final var player = context.requirePlayer();
        final var waystone = WaystoneRuleContext.getEffectiveWaystone(context).orElseThrow();
        if (waystone.hasOwner() && !waystone.isOwner(player)) {
            return Either.right(Component.translatable("chat.waystones.only_owner_can_edit"));
        }
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
            final var manageGlobalResult = mayManageGlobalWaystones.get(context);
            if (manageGlobalResult.right().isPresent()) {
                return manageGlobalResult;
            }
        }
        return Either.left(true);
    }).coerce(Coercion.BOOLEAN);

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

    private static final CachedShogiRule cachedWarpRequirements = CachedShogiRule.ofRules(scope, () -> WaystonesConfig.getActive().rules.warpRequirements);

    public static void initialize() {
        ConfigCallback.Reloaded.EVENT.register(schema -> {
            if (schema.identifier().equals(id("common"))) {
                cachedWarpRequirements.invalidate();
            }
        });
    }

    private static Either<?, ?> resolveWarpRequirements(WaystoneTeleportContext context) {
        return cachedWarpRequirements.get(context).apply(context);
    }
}
