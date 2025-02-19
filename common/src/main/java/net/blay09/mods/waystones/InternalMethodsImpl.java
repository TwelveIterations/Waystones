package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTeleportContext;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return WaystonesAPI.createCustomTeleportContext(entity, waystone).ifLeft(context -> {
            context.setWarpMode(warpMode);
            context.getLeashedEntities().addAll(PlayerWaystoneManager.findLeashedAnimals(entity));
            context.setFromWaystone(fromWaystone);
            context.setWarpItem(PlayerWaystoneManager.findWarpItem(entity, warpMode));
            context.setCooldown(PlayerWaystoneManager.getCooldownPeriod(warpMode, waystone));

            // Use the context so far to determine the xp cost
            context.setXpCost(PlayerWaystoneManager.getExperienceLevelCost(entity, waystone, warpMode, context));
        });
    }

    @Override
    public Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone) {
        if (!waystone.isValid()) {
            return Either.right(new WaystoneTeleportError.InvalidWaystone(waystone));
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return Either.right(new WaystoneTeleportError.NotOnServer());
        }

        ServerLevel targetLevel = server.getLevel(waystone.getDimension());
        if (targetLevel == null) {
            return Either.right(new WaystoneTeleportError.InvalidDimension(waystone.getDimension()));
        }

        if (!waystone.isValidInLevel(targetLevel)) {
            return Either.right(new WaystoneTeleportError.MissingWaystone(waystone));
        }

        return Either.left(new WaystoneTeleportContext(entity, waystone, waystone.resolveDestination(targetLevel)));
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, IWaystone fromWaystone) {
        return PlayerWaystoneManager.tryTeleportToWaystone(entity, waystone, warpMode, fromWaystone);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context) {
        return PlayerWaystoneManager.tryTeleport(context);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone) {
        return createDefaultTeleportContext(entity, waystone, WarpMode.CUSTOM, null).mapLeft(this::forceTeleport);
    }

    @Override
    public List<Entity> forceTeleport(IWaystoneTeleportContext context) {
        return PlayerWaystoneManager.doTeleport(context);
    }

    @Override
    public Optional<IWaystone> getWaystoneAt(Level level, BlockPos pos) {
        return WaystoneManager.get(level.getServer()).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> getWaystone(Level level, UUID uuid) {
        return WaystoneManager.get(level.getServer()).getWaystoneById(uuid);
    }

    @Override
    public ItemStack createAttunedShard(IWaystone warpPlate) {
        ItemStack itemStack = new ItemStack(ModItems.attunedShard);
        setBoundWaystone(itemStack, warpPlate);
        return itemStack;
    }

    @Override
    public ItemStack createBoundScroll(IWaystone waystone) {
        ItemStack itemStack = new ItemStack(ModItems.boundScroll);
        setBoundWaystone(itemStack, waystone);
        return itemStack;
    }

    @Override
    public Optional<IWaystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
        Block block = Balm.getRegistries().getBlock(style.getBlockRegistryName());
        level.setBlock(pos, block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        Block sharestone = color != null ? ModBlocks.scopedSharestones[color.ordinal()] : ModBlocks.sharestone;
        level.setBlock(pos, sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> placeWarpPlate(Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.warpPlate.defaultBlockState(), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> getBoundWaystone(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            return Optional.ofNullable(attunementItem.getWaystoneAttunedTo(Balm.getHooks().getServer(), itemStack));
        }
        return Optional.empty();
    }

    @Override
    public void setBoundWaystone(ItemStack itemStack, @Nullable IWaystone waystone) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            attunementItem.setWaystoneAttunedTo(itemStack, waystone);
        }
    }

    @Override
    public boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return PlayerWaystoneManager.isWaystoneActivated(player, waystone);
    }

    @Override
    public Collection<IWaystone> getActivatedWaystones(Player player) {
        return PlayerWaystoneManager.getWaystones(player);
    }

    @Override
    public Optional<IWaystone> getNearestWaystone(Player player) {
        return Optional.ofNullable(PlayerWaystoneManager.getNearestWaystone(player));
    }
}
