package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTeleportContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class WaystonesAPI {

    public static InternalMethods __internalMethods;

    public static Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return __internalMethods.createDefaultTeleportContext(entity, waystone, warpMode, fromWaystone);
    }

    public static Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone) {
        return __internalMethods.createCustomTeleportContext(entity, waystone);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return __internalMethods.tryTeleportToWaystone(entity, waystone, warpMode, fromWaystone);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    /**
     * @deprecated Use {@link #tryTeleport(IWaystoneTeleportContext)} instead.
     */
    @Deprecated
    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    public static Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone) {
        return __internalMethods.forceTeleportToWaystone(entity, waystone);
    }

    public static List<Entity> forceTeleport(IWaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }

    /**
     * @deprecated Use {@link #forceTeleport(IWaystoneTeleportContext)} instead.
     */
    @Deprecated
    public static List<Entity> forceTeleport(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }

    public static Optional<IWaystone> getWaystoneAt(ServerLevel level, BlockPos pos) {
        return __internalMethods.getWaystoneAt(level, pos);
    }

    public static Optional<IWaystone> getWaystoneAt(MinecraftServer server, BlockGetter blockGetter, BlockPos pos) {
        return __internalMethods.getWaystoneAt(server, blockGetter, pos);
    }

    /**
     * @param level only used to access getServer() when on server, does not have to match the waystone's actual level
     * @deprecated Use {@link #getWaystone(MinecraftServer, UUID)} instead.
     */
    @Deprecated
    public static Optional<IWaystone> getWaystone(Level level, UUID uuid) {
        return __internalMethods.getWaystone(level, uuid);
    }

    public static Optional<IWaystone> getWaystone(MinecraftServer server, UUID uuid) {
        return __internalMethods.getWaystone(server, uuid);
    }

    /**
     * Returns a stream of all waystones in the database. This includes regular waystones, sharestones and others.
     */
    public static Stream<IWaystone> getAllWaystones(MinecraftServer server) {
        return __internalMethods.getAllWaystones(server);
    }

    public static Stream<IWaystone> getWaystonesByType(MinecraftServer server, ResourceLocation type) {
        return __internalMethods.getWaystonesByType(server, type);
    }

    /**
     * Removes a waystone from the database. This does not destroy the block, it only removes the data stored for the waystone.
     */
    public static void removeWaystoneFromDatabase(MinecraftServer server, IWaystone waystone) {
        __internalMethods.removeWaystoneFromDatabase(server, waystone);
    }

    public static boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return __internalMethods.isWaystoneActivated(player, waystone);
    }

    public static Collection<IWaystone> getActivatedWaystones(Player player) {
        return __internalMethods.getActivatedWaystones(player);
    }

    public static Optional<IWaystone> getNearestWaystone(Player player) {
        return __internalMethods.getNearestWaystone(player);
    }

    public static Optional<IWaystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
        return __internalMethods.placeWaystone(level, pos, style);
    }

    public static Optional<IWaystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        return __internalMethods.placeSharestone(level, pos, color);
    }

    public static Optional<IWaystone> placeWarpPlate(Level level, BlockPos pos) {
        return __internalMethods.placeWarpPlate(level, pos);
    }

    public static Optional<IWaystone> getBoundWaystone(ItemStack itemStack) {
        return __internalMethods.getBoundWaystone(itemStack);
    }

    public static void setBoundWaystone(ItemStack itemStack, IWaystone waystone) {
        __internalMethods.setBoundWaystone(itemStack, waystone);
    }

    public static ItemStack createAttunedShard(IWaystone warpPlate) {
        return __internalMethods.createAttunedShard(warpPlate);
    }

    public static ItemStack createBoundScroll(IWaystone waystone) {
        return __internalMethods.createBoundScroll(waystone);
    }
}
