package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.platform.BalmEnvironment;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.MutableWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.trait.WaystoneKindScoped;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.store.InMemoryWaystonesPlayerStore;
import net.blay09.mods.waystones.store.PersistentWaystonesPlayerStore;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.blay09.mods.waystones.store.WaystonesPlayerStore;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGeneratorManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PlayerWaystoneManager {

    private static final RandomSource random = RandomSource.create();
    private static final WaystonesPlayerStore persistentPlayerWaystoneData = new PersistentWaystonesPlayerStore();
    private static final WaystonesPlayerStore inMemoryPlayerWaystoneData = new InMemoryWaystonesPlayerStore();

    public static boolean isWaystoneActivated(Player player, Waystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, Waystone waystone) {
        if (player.level() instanceof ServerLevel serverLevel) {
            if (waystone instanceof MutableWaystone mutableWaystone) {
                if (!waystone.hasName() && waystone.wasGenerated()) {
                    NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().worldGen.nameGenerationMode;
                    final var name = NameGeneratorManager.get(serverLevel.getServer()).getName(player.level(), waystone, random, nameGenerationMode);
                    mutableWaystone.setName(name);
                }

                if (!waystone.hasOwner()) {
                    mutableWaystone.setOwnerUid(player.getUUID());
                    mutableWaystone.setVisibility(WaystonesConfig.getActive().rules.defaultVisibility.getVisibility());
                    if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
                        PlayerWaystoneManager.activeWaystoneForEveryone(serverLevel.getServer(), waystone);
                    }
                }

                mutableWaystone.setSeen(true);
            }

            SavedDataWaystonesStore.get(serverLevel.getServer()).setDirty();
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)) {
            getPlayerWaystoneData(player.level()).activateWaystone(player, waystone);
            ensureWaystoneGroups(player, waystone);

            WaystoneActivatedEvent.EVENT.invoker().accept(new WaystoneActivatedEvent(player, waystone));
        }
    }

    public static Optional<Waystone> getInventoryButtonTarget(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return Waystones.getWaystoneStore(player).findWaystoneByName(inventoryButtonMode.getNamedTarget());
        }

        return Optional.empty();
    }

    public static void deactivateWaystone(Player player, Waystone waystone) {
        getPlayerWaystoneData(player.level()).deactivateWaystone(player, waystone);
    }

    public static Optional<Waystone> getNearestWaystone(Player player) {
        final var returnPortal = WarpPortalManager.getReturnPortal(player);
        if (returnPortal.isPresent()) {
            return returnPortal;
        }

        return getPlayerWaystoneData(player.level()).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level().dimension())
                .min((first, second) -> {
                    double firstDist = first.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    double secondDist = second.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                });
    }

    public static Collection<Waystone> getActivatedWaystones(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player);
    }

    public static Optional<Waystone> findWaystone(ServerPlayer player, UUID waystoneUid) {
        final var waystone = new WaystoneProxy(player.level().getServer(), waystoneUid);
        if (waystone.isValid()) {
            return Optional.of(waystone);
        }

        return TwinboundFeatherTargets.findTarget(player, waystoneUid);
    }

    public static Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid) {
        return getPlayerWaystoneData(player.level()).getWaystoneAlias(player, waystoneUid);
    }

    public static void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias) {
        getPlayerWaystoneData(player.level()).setWaystoneAlias(player, waystoneUid, alias);
    }

    public static Set<Identifier> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        return getPlayerWaystoneData(player.level()).getConfiguredWaystoneGroups(player, waystoneUid);
    }

    public static void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<Identifier> groupIds) {
        if (groupIds.contains(WaystoneGroups.FAVORITES.identifier())) {
            ensureWaystoneGroups(player, List.of(WaystoneGroups.FAVORITES));
        }
        getPlayerWaystoneData(player.level()).setConfiguredWaystoneGroups(player, waystoneUid, groupIds);
    }

    public static void ensureWaystoneGroups(Player player, Waystone waystone) {
        ensureWaystoneGroups(player, WaystoneGroups.getDynamicGroupDefinitions(waystone));
    }

    public static void ensureWaystoneGroups(Player player, Collection<WaystoneGroup> groups) {
        getPlayerWaystoneData(player.level()).addWaystoneGroups(player, groups);
    }

    public static Collection<WaystoneGroup> getWaystoneGroupRegistry(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystoneGroupRegistry(player);
    }

    public static UserDecoratedWaystone getPlayerDecoratedWaystone(Player player, Waystone waystone) {
        final var backingWaystone = waystone instanceof UserDecoratedWaystone userDecoratedWaystone ? userDecoratedWaystone.getBackingWaystone() : waystone;
        final var alias = getWaystoneAlias(player, backingWaystone.getWaystoneUid());
        final var configuredWaystoneGroups = getConfiguredWaystoneGroups(player, backingWaystone.getWaystoneUid());
        return new UserDecoratedWaystone(backingWaystone, alias.orElse(null), configuredWaystoneGroups);
    }

    public static List<UserDecoratedWaystone> getPlayerDecoratedWaystones(Player player, Collection<Waystone> waystones) {
        return waystones.stream()
                .map(waystone -> getPlayerDecoratedWaystone(player, waystone))
                .toList();
    }

    public static WaystonesPlayerStore getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static WaystonesPlayerStore getPlayerWaystoneData(BalmEnvironment side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static List<UUID> getSortingIndex(Player player) {
        return getPlayerWaystoneData(player.level()).getSortingIndex(player);
    }

    public static List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones) {
        return getPlayerWaystoneData(player.level()).ensureSortingIndex(player, waystones);
    }

    public static void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneAsFirst(player, waystoneUid);
    }

    public static void sortWaystoneAsLast(Player player, UUID waystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneAsLast(player, waystoneUid);
    }

    public static void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneSwap(player, waystoneUid, otherWaystoneUid);
    }

    public static void sortWaystoneGroupAsFirst(Player player, Identifier groupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupAsFirst(player, groupId);
    }

    public static void sortWaystoneGroupAsLast(Player player, Identifier groupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupAsLast(player, groupId);
    }

    public static void sortWaystoneGroupSwap(Player player, Identifier groupId, Identifier otherGroupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupSwap(player, groupId, otherGroupId);
    }

    public static void activeWaystoneForEveryone(@Nullable MinecraftServer server, Waystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (!isWaystoneActivated(player, waystone)) {
                activateWaystone(player, waystone);
            }
        }
    }

    public static void removeKnownWaystone(@Nullable MinecraftServer server, Waystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendActivatedWaystones(player);
        }
    }

    public static Collection<Waystone> getTargetsForPlayer(Player player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static Collection<Waystone> getTargetsForItem(Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof WaystoneKindScoped kindScoped) {
            final var result = new ArrayList<>(getTargetsForKind(player, kindScoped.getWaystoneKind()));
            // Twins are only available in Waystone-target teleports
            if (WaystoneKinds.WAYSTONE.equals(kindScoped.getWaystoneKind()) && player instanceof ServerPlayer serverPlayer) {
                result.addAll(TwinboundFeatherTargets.getTargets(serverPlayer));
            }
            return result;
        }
        final var result = new ArrayList<>(PlayerWaystoneManager.getActivatedWaystones(player));
        if (player instanceof ServerPlayer serverPlayer) {
            result.addAll(TwinboundFeatherTargets.getTargets(serverPlayer));
        }
        WarpPortalManager.getReturnPortal(player).ifPresent(result::add);
        return result;
    }

    public static Collection<Waystone> getTargetsForWaystone(Player player, Waystone waystone) {
        final var result = getTargetsForKind(player, waystone.getWaystoneKind());

        final var blockEntity = player.level().getBlockEntity(waystone.getPos());
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            result.addAll(waystoneBlockEntity.getAuxiliaryTargets());
        }
        // Twins are only available in Waystone-target teleports
        if (WaystoneKinds.WAYSTONE.equals(waystone.getWaystoneKind()) && player instanceof ServerPlayer serverPlayer) {
            result.addAll(TwinboundFeatherTargets.getTargets(serverPlayer));
        }
        WarpPortalManager.getReturnPortal(player).ifPresent(result::add);

        return result;
    }

    public static Collection<Waystone> getTargetsForKind(Player player, Identifier kind) {
        if (WaystoneKinds.WAYSTONE.equals(kind)) {
            return getTargetsForPlayer(player);
        } else if (WaystoneKinds.isSharestone(kind)) {
            return new ArrayList<>(SavedDataWaystonesStore.get(player.level().getServer()).getWaystonesByKind(kind));
        }
        return Collections.emptyList();
    }

    public static Collection<Waystone> getTargetsForInventoryButton(ServerPlayer player) {
        final var result = new ArrayList<>(PlayerWaystoneManager.getActivatedWaystones(player));
        result.addAll(TwinboundFeatherTargets.getTargets(player));
        return result;
    }
}
