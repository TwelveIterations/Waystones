package net.blay09.mods.waystones.config;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
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
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.config.rules.*;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystonesRules {

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

        it.registerSimpleEffect(id("is_portal_scroll"), context
                -> context.itemStack().is(ModItemTags.PORTAL_SCROLLS));

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

        it.registerSimpleEffect(id("is_warp_portal"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getWaystoneKind().equals(WaystoneKinds.WARP_PORTAL))
                .isPresent());

        it.registerSimpleEffect(id("is_fleeting_memorial"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getWaystoneKind().equals(WaystoneKinds.FLEETING_MEMORIAL))
                .isPresent());

        it.registerSimpleEffect(id("is_twinbound"), context
                -> WaystoneRuleContext.getEffectiveWaystone(context)
                .filter(waystone -> waystone.getWaystoneKind().equals(WaystoneKinds.TWINBOUND_FEATHER))
                .isPresent());

        for (final var sharestoneType : WaystoneKinds.SHARESTONES) {
            final Identifier sharestoneIdentifier = id("is_" + sharestoneType.getPath());
            it.registerSimpleEffect(sharestoneIdentifier, context
                    -> WaystoneRuleContext.getEffectiveWaystone(context)
                    .filter(waystone -> sharestoneType.equals(waystone.getWaystoneKind()))
                    .isPresent());
        }
    });

    public static final ShogiValue<WaystoneTeleportContext, List<?>> warpRequirements = scope.maybe(id("warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST);
    public static final ShogiValue<WaystoneTeleportContext, List<?>> inventoryButtonWarpRequirements = scope.maybe(id("inventory_button_warp_requirements"), WaystonesRules::resolveWarpRequirements).coerce(Coercion.LIST).networked();
    public static final ShogiValue<ShogiContext, List<?>> afterWarpEffects = scope.maybe(id("after_warp_effects"), (ShogiContext context) -> {
        if (!WaystonesConfig.getActive().rules.enableModifiers) {
            return Either.left(Collections.emptyList());
        }

        if (!(context.entity() instanceof LivingEntity livingEntity)) {
            return Either.left(Collections.emptyList());
        }

        if (!(context.blockEntity() instanceof BlockEntity blockEntity)) {
            return Either.left(Collections.emptyList());
        }

        final var container = Balm.capabilities().getCapability(blockEntity, CommonCapabilities.CONTAINER);

        int fireSeconds = 0;
        int poisonSeconds = 0;
        int blindSeconds = 0;
        int featherFallSeconds = 0;
        int fireResistanceSeconds = 0;
        int witherSeconds = 0;
        int potency = 1;
        List<ItemStack> curativeItems = new ArrayList<>();
        if (container != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack itemStack = container.getItem(i);
                if (itemStack.is(ModItemTags.WARP_MODIFIERS_SETS_ON_FIRE)) {
                    fireSeconds += itemStack.getCount();
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_POISONS)) {
                    poisonSeconds += itemStack.getCount();
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_BLINDS)) {
                    blindSeconds += itemStack.getCount();
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_CURES)) {
                    curativeItems.add(itemStack);
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_AMPLIFIES)) {
                    potency = Math.min(4, potency + itemStack.getCount());
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_FEATHER_FALLS)) {
                    featherFallSeconds = Math.min(8, featherFallSeconds + itemStack.getCount());
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_RESISTS_FIRE)) {
                    fireResistanceSeconds = Math.min(8, fireResistanceSeconds + itemStack.getCount());
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_WITHERS)) {
                    witherSeconds += itemStack.getCount();
                }
            }
        }

        if (fireSeconds > 0) {
            livingEntity.setRemainingFireTicks(fireSeconds * 20);
        }
        if (poisonSeconds > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, poisonSeconds * 20, potency));
        }
        if (blindSeconds > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindSeconds * 20, potency));
        }
        if (featherFallSeconds > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, featherFallSeconds * 20, potency));
        }
        if (fireResistanceSeconds > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireResistanceSeconds * 20, potency));
        }
        if (witherSeconds > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, witherSeconds * 20, potency));
        }
        if (!curativeItems.isEmpty()) {
            livingEntity.removeAllEffects();
        }
        return Either.left(Collections.emptyList());
    }).coerce(Coercion.LIST);
    public static final ShogiValue<Entity, Integer> warpStoneUseTime = scope.intValue(id("warp_stone_use_time"), _ -> 32);
    public static final ShogiValue<WarpPlateBlockEntity, Float> warpPlateUseTimeMultiplier = scope.floatValue(id("warp_plate_use_time_multiplier"), (WarpPlateBlockEntity context) -> {
        final var container = context.getContainer();
        float useTimeMultiplier = 1f;
        if (container != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack itemStack = container.getItem(i);
                if (itemStack.is(ModItemTags.WARP_MODIFIERS_SPEEDS_UP_WARP_PLATE)) {
                    useTimeMultiplier -= 0.016f * itemStack.getCount();
                } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_SLOWS_DOWN_WARP_PLATE)) {
                    useTimeMultiplier += 0.016f * itemStack.getCount();
                }
            }
        }
        return useTimeMultiplier;
    }).coerce(Coercion.FLOAT);

    public static final ShogiValue<WarpPlateBlockEntity, Integer> warpPlateUseTime = scope.intValue(id("warp_plate_use_time"), context -> (int) (Mth.clamp(15 * warpPlateUseTimeMultiplier.getOrDefault(context), 1, 30)));
    public static final ShogiValue<Entity, Integer> scrollUseTime = scope.intValue(id("scroll_use_time"), _ -> 32);

    public static final ShogiValue<ShogiContext, Boolean> mayManageGlobalWaystones = scope.maybe(id("may_manage_global_waystones"), (ShogiContext context) -> {
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

    public static final ShogiValue<ShogiContext, Boolean> mayEdit = scope.maybe(id("may_edit"), (ShogiContext context) -> {
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

    private static final CachedShogiRule cachedWarpRequirements = CachedShogiRule.ofRules(scope, WaystonesRules::getWarpRequirementsWithSettings);

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

    private static List<String> getWarpRequirementsWithSettings() {
        final var rules = WaystonesConfig.getActive().rules;
        final var warpSettings = rules.warpSettings;
        final var warpRequirements = rules.warpRequirements;
        final var result = new ArrayList<String>(warpSettings.size() + warpRequirements.size());
        result.addAll(warpSettings);
        result.addAll(warpRequirements);
        return result;
    }
}
