package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class WaystonesAPI {

    private static final InternalMethods __internalMethods;

    static {
        try {
            __internalMethods = (InternalMethods) Class.forName("net.blay09.mods.waystones.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return __internalMethods.createDefaultTeleportContext(entity, waystone, init);
    }

    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createCustomTeleportContext(entity, waystone);
    }

    public static WaystoneTeleportContext createUnboundTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createUnboundTeleportContext(entity, waystone);
    }

    public static WaystoneTeleportContext createUnboundTeleportContext(Entity entity) {
        return __internalMethods.createUnboundTeleportContext(entity);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    public static Either<List<Entity>, WaystoneTeleportError> forceTeleport(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }

    public static Optional<Waystone> getWaystoneAt(ServerLevel level, BlockPos pos) {
        return __internalMethods.getWaystoneAt(level, pos);
    }

    public static Optional<Waystone> getWaystoneAt(MinecraftServer server, BlockGetter blockGetter, BlockPos pos) {
        return __internalMethods.getWaystoneAt(server, blockGetter, pos);
    }

    public static Optional<Waystone> getWaystone(MinecraftServer server, UUID uuid) {
        return __internalMethods.getWaystone(server, uuid);
    }

    public static boolean isWaystoneActivated(Player player, Waystone waystone) {
        return __internalMethods.isWaystoneActivated(player, waystone);
    }

    /**
     * Returns a collection of all waystones in the database. This includes regular waystones, sharestones and others.
     */
    public static Collection<Waystone> getAllWaystones(MinecraftServer server) {
        return __internalMethods.getAllWaystones(server);
    }

    public static Collection<Waystone> getWaystonesByType(MinecraftServer server, Identifier type) {
        return __internalMethods.getWaystonesByType(server, type);
    }

    /**
     * Removes a waystone from the database. This does not destroy the block, it only removes the data stored for the waystone.
     */
    public static void removeWaystoneFromDatabase(MinecraftServer server, Waystone waystone) {
        __internalMethods.removeWaystoneFromDatabase(server, waystone);
    }

    public static Collection<Waystone> getActivatedWaystones(Player player) {
        return __internalMethods.getActivatedWaystones(player);
    }

    public static Optional<Waystone> getNearestWaystone(Player player) {
        return __internalMethods.getNearestWaystone(player);
    }

    public static Optional<Waystone> placeWaystone(ServerLevel level, BlockPos pos, WaystoneType type) {
        return __internalMethods.placeWaystone(level, pos, type);
    }

    public static Optional<Waystone> placeSharestone(ServerLevel level, BlockPos pos, SharestoneType type) {
        return __internalMethods.placeSharestone(level, pos, type);
    }

    public static Optional<Waystone> placeWarpPlate(ServerLevel level, BlockPos pos) {
        return __internalMethods.placeWarpPlate(level, pos);
    }

    public static Optional<Waystone> getBoundWaystone(@Nullable Player player, ItemStack itemStack) {
        return __internalMethods.getBoundWaystone(player, itemStack);
    }

    public static void setBoundWaystone(ItemStack itemStack, Waystone waystone) {
        __internalMethods.setBoundWaystone(itemStack, waystone);
    }

    public static ItemStack createAttunedShard(Waystone warpPlate) {
        return __internalMethods.createAttunedShard(warpPlate);
    }

    public static ItemStack createBoundScroll(Waystone waystone) {
        return __internalMethods.createBoundScroll(waystone);
    }

    public static void activateWaystone(ServerPlayer player, Waystone waystone) {
        __internalMethods.activateWaystone(player, waystone);
    }

    public static void deactivateWaystone(ServerPlayer player, Waystone waystone) {
        __internalMethods.deactivateWaystone(player, waystone);
    }

    public static Set<Identifier> getDynamicWaystoneGroups(Waystone waystone) {
        return WaystoneGroups.getDynamicGroups(waystone);
    }
}
