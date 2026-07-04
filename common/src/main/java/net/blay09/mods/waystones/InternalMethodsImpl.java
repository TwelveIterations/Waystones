package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.requirement.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.requirement.RequirementModifierParser;
import net.blay09.mods.waystones.requirement.WarpRequirementsContextImpl;
import net.blay09.mods.waystones.requirement.RequirementRegistry;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return createDefaultTeleportContext(createCustomTeleportContext(entity, waystone), entity, init);
    }

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createUncheckedDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return createDefaultTeleportContext(createUncheckedCustomTeleportContext(entity, waystone), entity, init);
    }

    private Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Either<WaystoneTeleportContext, WaystoneTeleportError> contextResult, Entity entity, Consumer<WaystoneTeleportContext> init) {
        return contextResult.ifLeft(context -> {
            final var config = WaystonesConfig.getActive();
            final var shouldTransportPets = config.teleports.transportPets;
            if (shouldTransportPets == WaystonesConfigData.TransportMobs.ENABLED || (shouldTransportPets == WaystonesConfigData.TransportMobs.SAME_DIMENSION && !context.isDimensionalTeleport())) {
                context.getAdditionalEntities().addAll(WaystoneTeleportManager.findPets(entity));
            }
            context.getLeashedEntities().addAll(WaystoneTeleportManager.findLeashedAnimals(entity));
            context.setAppliesModifiers(config.teleports.enableModifiers);
            init.accept(context);
            context.setRequirements(resolveRequirements(context));
        });
    }

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
        return createUncheckedCustomTeleportContext(entity, waystone).flatMap(context -> {
            ServerLevel targetLevel = entity.level().getServer().getLevel(waystone.getDimension());
            if (targetLevel == null) {
                return Either.right(new WaystoneTeleportError.InvalidDimension(waystone.getDimension()));
            }

            if (!waystone.isValidInLevel(targetLevel)) {
                return Either.right(new WaystoneTeleportError.MissingWaystone(waystone));
            }

            return Either.left(context);
        });
    }

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createUncheckedCustomTeleportContext(Entity entity, Waystone waystone) {
        if (!waystone.isValid()) {
            return Either.right(new WaystoneTeleportError.InvalidWaystone(waystone));
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            throw new IllegalStateException("must only be called with a server-side entity");
        }

        ServerLevel targetLevel = server.getLevel(waystone.getDimension());
        if (targetLevel == null) {
            return Either.right(new WaystoneTeleportError.InvalidDimension(waystone.getDimension()));
        }

        return Either.left(new WaystoneTeleportContextImpl(entity, waystone));
    }

    @Override
    public WaystoneTeleportContext createUnboundTeleportContext(Entity entity, Waystone waystone) {
        return new WaystoneTeleportContextImpl(entity, waystone);
    }

    @Override
    public WaystoneTeleportContext createUnboundTeleportContext(Entity entity) {
        return new WaystoneTeleportContextImpl(entity, InvalidWaystone.INSTANCE);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return toLegacyTeleportResult(WaystoneTeleportManager.tryTeleport(context));
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> forceTeleport(WaystoneTeleportContext context) {
        return toLegacyTeleportResult(WaystoneTeleportManager.teleport(context));
    }

    @Override
    public CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> tryTeleportAsync(WaystoneTeleportContext context) {
        return WaystoneTeleportManager.tryTeleportAsync(context).thenApply(this::toLegacyTeleportResult);
    }

    @Override
    public CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> forceTeleportAsync(WaystoneTeleportContext context) {
        return WaystoneTeleportManager.forceTeleportAsync(context).thenApply(this::toLegacyTeleportResult);
    }

    private Either<List<Entity>, WaystoneTeleportError> toLegacyTeleportResult(WaystoneTeleportResult result) {
        return result.error()
                .<Either<List<Entity>, WaystoneTeleportError>>map(Either::right)
                .orElseGet(() -> Either.left(result.teleportedEntities()));
    }

    @Override
    public Optional<Waystone> getWaystoneAt(Level level, BlockPos pos) {
        return WaystoneManagerImpl.get(level.getServer()).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(MinecraftServer server, BlockGetter level, BlockPos pos) {
        return WaystoneManagerImpl.get(server).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getWaystone(Level level, UUID uuid) {
        return WaystoneManagerImpl.get(level.getServer()).getWaystoneById(uuid);
    }

    @Override
    public Optional<Waystone> getWaystone(MinecraftServer server, UUID uuid) {
        return WaystoneManagerImpl.get(server).getWaystoneById(uuid);
    }

    @Override
    public ItemStack createAttunedShard(Waystone warpPlate) {
        ItemStack itemStack = new ItemStack(ModItems.attunedShard);
        setBoundWaystone(itemStack, warpPlate);
        return itemStack;
    }

    @Override
    public ItemStack createBoundScroll(Waystone waystone) {
        ItemStack itemStack = new ItemStack(ModItems.warpScroll);
        setBoundWaystone(itemStack, waystone);
        return itemStack;
    }

    @Override
    public Optional<Waystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
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
    public Optional<Waystone> placeSharestone(Level level, BlockPos pos, DyeColor color) {
        final var sharestone = ModBlocks.getSharestone(color);
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
    public Optional<Waystone> placeWarpPlate(Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.warpPlate.defaultBlockState()
                .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.PLAYER), 3);
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase waystoneBlockEntity && level instanceof ServerLevel serverLevel) {
            waystoneBlockEntity.initializeWaystone(serverLevel, null, WaystoneOrigin.PLAYER);
            return Optional.of(waystoneBlockEntity.getWaystone());
        }
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getBoundWaystone(@Nullable Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            return attunementItem.getWaystoneAttunedTo(Balm.getHooks().getServer(), player, itemStack);
        }
        return Optional.empty();
    }

    @Override
    public void setBoundWaystone(ItemStack itemStack, @Nullable Waystone waystone) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            attunementItem.setWaystoneAttunedTo(itemStack, waystone);
        }
    }

    @Override
    public WarpRequirement resolveRequirements(WaystoneTeleportContext context) {
        final var requirementsContext = new WarpRequirementsContextImpl(context);
        final var configuredModifiers = WaystonesConfig.getActive().teleports.warpRequirements;
        for (final var modifier : configuredModifiers) {
            if (modifier.isBlank()) {
                continue;
            }

            RequirementModifierParser.parse(modifier)
                    .stream()
                    .filter(configuredModifier -> configuredModifier.requirement().modifier().isEnabled())
                    .forEach(requirementsContext::apply);
        }

        return requirementsContext.resolve();
    }

    @Override
    public void registerRequirementType(RequirementType<?> requirementType) {
        RequirementRegistry.register(requirementType);
    }

    @Override
    public void registerRequirementModifier(RequirementFunction<?, ?> requirementModifier) {
        RequirementRegistry.register(requirementModifier);
    }

    @Override
    public void registerVariableResolver(VariableResolver variableResolver) {
        RequirementRegistry.register(variableResolver);
    }

    @Override
    public void registerConditionResolver(ConditionResolver<?> conditionResolver) {
        RequirementRegistry.register(conditionResolver);
    }

    @Override
    public void registerParameterSerializer(ParameterSerializer<?> parameterSerializer) {
        RequirementRegistry.register(parameterSerializer);
    }

    @Override
    public boolean isWaystoneActivated(Player player, Waystone waystone) {
        return PlayerWaystoneManager.isWaystoneActivated(player, waystone);
    }

    @Override
    public Collection<Waystone> getActivatedWaystones(Player player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    @Override
    public Optional<Waystone> getNearestWaystone(Player player) {
        return PlayerWaystoneManager.getNearestWaystone(player);
    }

    @Override
    public void activateWaystone(ServerPlayer player, Waystone waystone) {
        PlayerWaystoneManager.activateWaystone(player, waystone);
        WaystoneSyncManager.sendActivatedWaystones(player);
    }

    @Override
    public void deactivateWaystone(ServerPlayer player, Waystone waystone) {
        PlayerWaystoneManager.deactivateWaystone(player, waystone);
        WaystoneSyncManager.sendActivatedWaystones(player);
    }

    @Override
    public Stream<Waystone> getAllWaystones(MinecraftServer server) {
        return WaystoneManagerImpl.get(server).getWaystones();
    }

    @Override
    public Stream<Waystone> getWaystonesByType(MinecraftServer server, ResourceLocation type) {
        return WaystoneManagerImpl.get(server).getWaystonesByType(type);
    }

    @Override
    public void removeWaystoneFromDatabase(MinecraftServer server, Waystone waystone) {
        WaystoneManagerImpl.get(server).removeWaystone(waystone);
        PlayerWaystoneManager.removeKnownWaystone(server, waystone);
    }

    @Override
    public Optional<TeleportDestination> resolveDefaultDestination(ServerLevel level, Waystone waystone) {
        return WaystoneTeleportManager.resolveDefaultDestination(level, waystone);
    }
}
