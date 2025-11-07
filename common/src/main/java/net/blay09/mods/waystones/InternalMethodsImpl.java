package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.requirement.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.requirement.RequirementModifierParser;
import net.blay09.mods.waystones.requirement.WarpRequirementsContextImpl;
import net.blay09.mods.waystones.requirement.RequirementRegistry;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return WaystonesAPI.createCustomTeleportContext(entity, waystone).ifLeft(context -> {
            final var config = WaystonesConfig.getActive();
            final var shouldTransportPets = config.teleports.transportPets;
            if (shouldTransportPets == WaystonesConfig.TransportMobs.ENABLED || (shouldTransportPets == WaystonesConfig.TransportMobs.SAME_DIMENSION && !context.isDimensionalTeleport())) {
                if (entity instanceof LivingEntity livingEntity) {
                    context.getAdditionalEntities().addAll(WaystoneTeleportManager.findPets(livingEntity));
                }
            }
            context.getLeashedEntities().addAll(WaystoneTeleportManager.findLeashedAnimals(entity));
            context.setAppliesModifiers(config.teleports.enableModifiers);
            init.accept(context);
            context.setRequirements(resolveRequirements(context));
        });
    }

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
        if (!waystone.isValid()) {
            return Either.right(new WaystoneTeleportError.InvalidWaystone(waystone));
        }

        MinecraftServer server = entity.level().getServer();
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
        return WaystoneTeleportManager.tryTeleport(context);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> forceTeleport(WaystoneTeleportContext context) {
        return WaystoneTeleportManager.doTeleport(context);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(ServerLevel level, BlockPos pos) {
        return SavedDataWaystonesStore.get(level.getServer()).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(MinecraftServer server, BlockGetter level, BlockPos pos) {
        return SavedDataWaystonesStore.get(server).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getWaystone(MinecraftServer server, UUID uuid) {
        return SavedDataWaystonesStore.get(server).getWaystoneById(uuid);
    }

    @Override
    public ItemStack createAttunedShard(Waystone warpPlate) {
        ItemStack itemStack = ModItems.attunedShard.createStack();
        setBoundWaystone(itemStack, warpPlate);
        return itemStack;
    }

    @Override
    public ItemStack createBoundScroll(Waystone waystone) {
        ItemStack itemStack = ModItems.warpScroll.createStack();
        setBoundWaystone(itemStack, waystone);
        return itemStack;
    }

    @Override
    public Optional<Waystone> placeWaystone(ServerLevel level, BlockPos pos, WaystoneStyle style) {
        Block block = BuiltInRegistries.BLOCK.getValue(style.getBlockRegistryName());
        level.setBlock(pos, block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> placeSharestone(ServerLevel level, BlockPos pos, DyeColor color) {
        final var sharestone = ModBlocks.sharestones.get(color);
        if (sharestone == null) {
            return Optional.empty();
        }

        level.setBlock(pos, sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> placeWarpPlate(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.warpPlate.defaultBlockState(), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getBoundWaystone(@Nullable Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            return attunementItem.getWaystoneAttunedTo(Balm.platform().server(), player, itemStack);
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
    public Collection<Waystone> getAllWaystones(MinecraftServer server) {
        return SavedDataWaystonesStore.get(server).getWaystones();
    }

    @Override
    public Collection<Waystone> getWaystonesByType(MinecraftServer server, ResourceLocation type) {
        return SavedDataWaystonesStore.get(server).getWaystonesByType(type);
    }

    @Override
    public void removeWaystoneFromDatabase(MinecraftServer server, Waystone waystone) {
        SavedDataWaystonesStore.get(server).removeWaystone(waystone);
        PlayerWaystoneManager.removeKnownWaystone(server, waystone);
    }
}
