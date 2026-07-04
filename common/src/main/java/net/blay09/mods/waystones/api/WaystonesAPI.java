package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.requirement.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class WaystonesAPI {

    public static InternalMethods __internalMethods;

    /**
     * @deprecated Use {@link #createUncheckedDefaultTeleportContext(Entity, Waystone, Consumer)} instead, which avoids a synchronous chunk load by delaying some errors to later teleport stages.
     */
    @Deprecated
    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return __internalMethods.createDefaultTeleportContext(entity, waystone, init);
    }

    /**
     * @deprecated Use {@link #createUncheckedCustomTeleportContext(Entity, Waystone)} instead, which avoids a synchronous chunk load by delaying some errors to later teleport stages.
     */
    @Deprecated
    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createCustomTeleportContext(entity, waystone);
    }

    /**
     * Creates a teleport context without validating the target waystone against its destination level. This avoids destination chunk reads during
     * context creation; {@link #tryTeleportAsync(WaystoneTeleportContext)} performs that validation after the destination chunk has loaded.
     */
    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createUncheckedDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return __internalMethods.createUncheckedDefaultTeleportContext(entity, waystone, init);
    }

    /**
     * Creates a teleport context without validating the target waystone against its destination level. This avoids destination chunk reads during
     * context creation; {@link #tryTeleportAsync(WaystoneTeleportContext)} performs that validation after the destination chunk has loaded.
     */
    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createUncheckedCustomTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createUncheckedCustomTeleportContext(entity, waystone);
    }

    public static WaystoneTeleportContext createUnboundTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createUnboundTeleportContext(entity, waystone);
    }

    public static WaystoneTeleportContext createUnboundTeleportContext(Entity entity) {
        return __internalMethods.createUnboundTeleportContext(entity);
    }

    /**
     * @deprecated Use {@link #tryTeleportAsync(WaystoneTeleportContext)} to avoid blocking while destination chunks are loaded.
     */
    @Deprecated
    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    /**
     * @deprecated Use {@link #forceTeleportAsync(WaystoneTeleportContext)} to avoid blocking while destination chunks are loaded.
     */
    @Deprecated
    public static Either<List<Entity>, WaystoneTeleportError> forceTeleport(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }

    public static CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> tryTeleportAsync(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleportAsync(context);
    }

    public static CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> forceTeleportAsync(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleportAsync(context);
    }

    public static Optional<Waystone> getWaystoneAt(ServerLevel level, BlockPos pos) {
        return __internalMethods.getWaystoneAt(level, pos);
    }

    public static Optional<Waystone> getWaystoneAt(MinecraftServer server, BlockGetter blockGetter, BlockPos pos) {
        return __internalMethods.getWaystoneAt(server, blockGetter, pos);
    }

    /**
     * @param level only used to access getServer() when on server, does not have to match the waystone's actual level
     * @deprecated Use {@link #getWaystone(MinecraftServer, UUID)} instead.
     */
    @Deprecated
    public static Optional<Waystone> getWaystone(Level level, UUID uuid) {
        return __internalMethods.getWaystone(level, uuid);
    }

    public static Optional<Waystone> getWaystone(MinecraftServer server, UUID uuid) {
        return __internalMethods.getWaystone(server, uuid);
    }

    public static boolean isWaystoneActivated(Player player, Waystone waystone) {
        return __internalMethods.isWaystoneActivated(player, waystone);
    }

    /**
     * Returns a stream of all waystones in the database. This includes regular waystones, sharestones and others.
     */
    public static Stream<Waystone> getAllWaystones(MinecraftServer server) {
        return __internalMethods.getAllWaystones(server);
    }

    public static Stream<Waystone> getWaystonesByType(MinecraftServer server, ResourceLocation type) {
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

    public static Optional<Waystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
        return __internalMethods.placeWaystone(level, pos, style);
    }

    public static Optional<Waystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        return __internalMethods.placeSharestone(level, pos, color);
    }

    public static Optional<Waystone> placeWarpPlate(Level level, BlockPos pos) {
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

    public static WarpRequirement resolveRequirements(WaystoneTeleportContext context) {
        return __internalMethods.resolveRequirements(context);
    }

    public static void registerRequirementType(RequirementType<?> requirementType) {
        __internalMethods.registerRequirementType(requirementType);
    }

    public static void registerRequirementModifier(RequirementFunction<?, ?> requirementModifier) {
        __internalMethods.registerRequirementModifier(requirementModifier);
    }

    public static void registerVariableResolver(VariableResolver variableResolver) {
        __internalMethods.registerVariableResolver(variableResolver);
    }

    public static void registerConditionPredicate(ConditionResolver<?> conditionResolver) {
        __internalMethods.registerConditionResolver(conditionResolver);
    }

    public static void registerParameterSerializer(ParameterSerializer<?> parameterSerializer) {
        __internalMethods.registerParameterSerializer(parameterSerializer);
    }

    public static void activateWaystone(ServerPlayer player, Waystone waystone) {
        __internalMethods.activateWaystone(player, waystone);
    }

    public static void deactivateWaystone(ServerPlayer player, Waystone waystone) {
        __internalMethods.deactivateWaystone(player, waystone);
    }

    public static Set<ResourceLocation> getDynamicWaystoneGroups(Waystone waystone) {
        return WaystoneGroups.getDynamicGroups(waystone);
    }

    /**
     * Resolves the default teleport destination for the given waystone.
     */
    public static Optional<TeleportDestination> resolveDefaultDestination(ServerLevel level, Waystone waystone) {
        return __internalMethods.resolveDefaultDestination(level, waystone);
    }
}
