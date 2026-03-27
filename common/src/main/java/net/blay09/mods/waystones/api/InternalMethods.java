package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface InternalMethods {

    Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init);

    Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone);

    WaystoneTeleportContext createUnboundTeleportContext(Entity entity, Waystone waystone);

    WaystoneTeleportContext createUnboundTeleportContext(Entity entity);

    Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context);

    Either<List<Entity>, WaystoneTeleportError> forceTeleport(WaystoneTeleportContext context);

    Optional<Waystone> getWaystoneAt(ServerLevel level, BlockPos pos);

    ItemStack createAttunedShard(Waystone warpPlate);

    ItemStack createBoundScroll(Waystone waystone);

    Optional<Waystone> placeWaystone(ServerLevel level, BlockPos pos, WaystoneType style);

    Optional<Waystone> placeSharestone(ServerLevel level, BlockPos pos, SharestoneType type);

    Optional<Waystone> placeWarpPlate(ServerLevel level, BlockPos pos);

    Optional<Waystone> getBoundWaystone(@Nullable Player player, ItemStack itemStack);

    void setBoundWaystone(ItemStack itemStack, @Nullable Waystone waystone);

    Optional<Waystone> getWaystoneAt(MinecraftServer server, BlockGetter level, BlockPos pos);

    Optional<Waystone> getWaystone(MinecraftServer level, UUID uuid);

    boolean isWaystoneActivated(Player player, Waystone waystone);

    Collection<Waystone> getActivatedWaystones(Player player);

    void activateWaystone(ServerPlayer player, Waystone waystone);

    void deactivateWaystone(ServerPlayer player, Waystone waystone);

    Optional<Waystone> getNearestWaystone(Player player);

    Collection<Waystone> getAllWaystones(MinecraftServer server);

    Collection<Waystone> getWaystonesByType(MinecraftServer server, Identifier type);

    void removeWaystoneFromDatabase(MinecraftServer server, Waystone waystone);
}
