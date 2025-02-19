package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public enum WarpMode {
    INVENTORY_BUTTON(() -> WaystonesConfig.getActive().xpCost.inventoryButtonXpCostMultiplier, WarpMode::waystoneIsActivatedOrNamed, false),
    WARP_SCROLL(() -> 0.0, WarpMode::waystoneIsActivated, true),
    RETURN_SCROLL(() -> 0.0, WarpMode::waystoneIsActivated, true),
    BOUND_SCROLL(() -> 0.0, WarpMode::always, true),
    WARP_STONE(() -> WaystonesConfig.getActive().xpCost.warpStoneXpCostMultiplier, WarpMode::waystoneIsActivated, false),
    WAYSTONE_TO_WAYSTONE(() -> WaystonesConfig.getActive().xpCost.waystoneXpCostMultiplier, WarpMode::waystoneIsActivated, false),
    SHARESTONE_TO_SHARESTONE(() -> WaystonesConfig.getActive().xpCost.sharestoneXpCostMultiplier, WarpMode::sharestonesOnly, false),
    WARP_PLATE(() -> WaystonesConfig.getActive().xpCost.warpPlateXpCostMultiplier, WarpMode::warpPlatesOnly, false),
    PORTSTONE_TO_WAYSTONE(() -> WaystonesConfig.getActive().xpCost.portstoneXpCostMultiplier, WarpMode::waystoneIsActivated, false),
    CUSTOM(() -> 0.0, WarpMode::always, false);

    public static WarpMode[] values = values();

    private final Supplier<Double> xpCostMultiplierSupplier;
    private final BiPredicate<Entity, IWaystone> allowTeleportPredicate;
    private final boolean consumesItem;

    WarpMode(Supplier<Double> xpCostMultiplierSupplier, BiPredicate<Entity, IWaystone> allowTeleportPredicate, boolean consumesItem) {
        this.xpCostMultiplierSupplier = xpCostMultiplierSupplier;
        this.allowTeleportPredicate = allowTeleportPredicate;
        this.consumesItem = consumesItem;
    }

    public double getXpCostMultiplier() {
        return xpCostMultiplierSupplier.get();
    }

    public boolean consumesItem() {
        return consumesItem;
    }

    private static boolean always(Entity player, IWaystone waystone) {
        return true;
    }

    private static boolean waystoneIsActivatedOrNamed(Entity player, IWaystone waystone) {
        return WaystonesConfig.getActive().getInventoryButtonMode().hasNamedTarget() || (waystone.getWaystoneType()
                .equals(WaystoneTypes.WAYSTONE) && player instanceof Player && PlayerWaystoneManager.isWaystoneActivated(((Player) player), waystone));
    }

    private static boolean waystoneIsActivated(Entity player, IWaystone waystone) {
        return waystone.getWaystoneType()
                .equals(WaystoneTypes.WAYSTONE) && player instanceof Player && PlayerWaystoneManager.isWaystoneActivated(((Player) player), waystone);
    }

    private static boolean sharestonesOnly(Entity player, IWaystone waystone) {
        return WaystoneTypes.isSharestone(waystone.getWaystoneType());
    }

    private static boolean warpPlatesOnly(Entity player, IWaystone waystone) {
        return waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
    }

    public BiPredicate<Entity, IWaystone> getAllowTeleportPredicate() {
        return allowTeleportPredicate;
    }
}
