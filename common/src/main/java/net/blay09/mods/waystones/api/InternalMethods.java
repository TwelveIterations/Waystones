package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InternalMethods {

    Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone);

    Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone);

    Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, IWaystone fromWaystone);

    Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context);

    Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone);

    List<Entity> forceTeleport(IWaystoneTeleportContext context);

    Optional<IWaystone> getWaystoneAt(Level level, BlockPos pos);

    Optional<IWaystone> getWaystone(Level level, UUID uuid);

    ItemStack createAttunedShard(IWaystone warpPlate);

    ItemStack createBoundScroll(IWaystone waystone);

    Optional<IWaystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style);

    Optional<IWaystone> placeSharestone(Level level, BlockPos pos, DyeColor color);

    Optional<IWaystone> placeWarpPlate(Level level, BlockPos pos);

    Optional<IWaystone> getBoundWaystone(ItemStack itemStack);

    void setBoundWaystone(ItemStack itemStack, @Nullable IWaystone waystone);

    boolean isWaystoneActivated(Player player, IWaystone waystone);

    Collection<IWaystone> getActivatedWaystones(Player player);

    Optional<IWaystone> getNearestWaystone(Player player);
}
