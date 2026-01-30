package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTeleportContext;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
    public Optional<IWaystone> getWaystoneAt(MinecraftServer server, BlockGetter level, BlockPos pos) {
        return WaystoneManager.get(server).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> getWaystone(Level level, UUID uuid) {
        return WaystoneManager.get(level.getServer()).getWaystoneById(uuid);
    }

    @Override
    public Optional<IWaystone> getWaystone(MinecraftServer server, UUID uuid) {
        return WaystoneManager.get(server).getWaystoneById(uuid);
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
        Block block = BuiltInRegistries.BLOCK.get(style.getBlockRegistryName());
        level.setBlock(pos, block.defaultBlockState()
                .setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        level.setBlock(pos.above(), block.defaultBlockState()
                .setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase waystoneBlockEntity && level instanceof ServerLevel serverLevel) {
            waystoneBlockEntity.initializeWaystone(serverLevel, null, WaystoneOrigin.PLAYER);
            if (level.getBlockEntity(pos.above()) instanceof WaystoneBlockEntityBase waystoneBlockEntityAbove) {
                waystoneBlockEntityAbove.initializeFromBase(waystoneBlockEntity);
            }
            return Optional.of(waystoneBlockEntity.getWaystone());
        }
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        final var sharestone = color != null ? ModBlocks.scopedSharestones[color.ordinal()] : ModBlocks.sharestone;
        if (sharestone == null) {
            return Optional.empty();
        }

        level.setBlock(pos, sharestone.defaultBlockState()
                .setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        level.setBlock(pos.above(), sharestone.defaultBlockState()
                .setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase waystoneBlockEntity && level instanceof ServerLevel serverLevel) {
            waystoneBlockEntity.initializeWaystone(serverLevel, null, WaystoneOrigin.PLAYER);
            if (level.getBlockEntity(pos.above()) instanceof WaystoneBlockEntityBase waystoneBlockEntityAbove) {
                waystoneBlockEntityAbove.initializeFromBase(waystoneBlockEntity);
            }
            return Optional.of(waystoneBlockEntity.getWaystone());
        }
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<IWaystone> placeWarpPlate(Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.warpPlate.defaultBlockState()
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase waystoneBlockEntity && level instanceof ServerLevel serverLevel) {
            waystoneBlockEntity.initializeWaystone(serverLevel, null, WaystoneOrigin.PLAYER);
            return Optional.of(waystoneBlockEntity.getWaystone());
        }
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

    @Override
    public Stream<IWaystone> getAllWaystones(MinecraftServer server) {
        return WaystoneManager.get(server).getWaystones();
    }

    @Override
    public Stream<IWaystone> getWaystonesByType(MinecraftServer server, ResourceLocation type) {
        return WaystoneManager.get(server).getWaystonesByType(type);
    }

    @Override
    public void removeWaystoneFromDatabase(MinecraftServer server, IWaystone waystone) {
        WaystoneManager.get(server).removeWaystone(waystone);
        PlayerWaystoneManager.removeKnownWaystone(server, waystone);
    }
}
