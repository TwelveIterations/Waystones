package net.blay09.mods.waystones.core;

import com.mojang.authlib.GameProfile;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.WaystoneSortMode;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGeneratorManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerWaystoneManager {

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean isWaystoneActivated(Player player, Waystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, Waystone waystone) {
        if (!waystone.hasName() && waystone instanceof MutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().worldGen.nameGenerationMode;
            final var name = NameGeneratorManager.get(player.getServer()).getName(player.level(), waystone, player.level().random, nameGenerationMode);
            ((MutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof MutableWaystone mutableWaystone) {
            final var previousVisibility = waystone.getVisibility();
            mutableWaystone.setOwnerUid(player.getUUID());
            mutableWaystone.setOwnerUsername(player.getGameProfile().getName());
            mutableWaystone.setVisibility(WaystoneVisibility.fromWaystoneType(waystone.getWaystoneType()));
            if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
                WaystoneIndexManager.visibilityChanged(player.getServer(), waystone, previousVisibility);
            }
        }

        if (player.getServer() != null) {
            WaystoneManagerImpl.get(player.getServer()).setDirty();
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.level()).activateWaystone(player, waystone);
            ensureWaystoneGroups(player, waystone);

            Balm.getEvents().fireEvent(new WaystoneActivatedEvent(player, waystone));
        }
    }

    public static Optional<Waystone> getInventoryButtonTarget(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return getPlayerWaystoneData(player.level()).findWaystoneByName(player, inventoryButtonMode.getNamedTarget());
        }

        return Optional.empty();
    }

    public static void deactivateWaystone(Player player, Waystone waystone) {
        getPlayerWaystoneData(player.level()).deactivateWaystone(player, waystone);
    }

    public static Map<ResourceLocation, Long> getCooldowns(Player player) {
        return getPlayerWaystoneData(player.level()).getCooldowns(player);
    }

    public static void resetCooldowns(Player player) {
        getPlayerWaystoneData(player.level()).resetCooldowns(player);
    }

    public static long getCooldownUntil(Player player, ResourceLocation key) {
        return getPlayerWaystoneData(player.level()).getCooldownUntil(player, key);
    }

    public static long getCooldownMillisLeft(Player player, ResourceLocation key) {
        long cooldownUntil = getCooldownUntil(player, key);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setCooldownUntil(Player player, ResourceLocation key, long timestamp) {
        getPlayerWaystoneData(player.level()).setCooldownUntil(player, key, timestamp);
    }

    public static Optional<Waystone> getNearestWaystone(Player player) {
        final var returnPortal = WarpPortalManager.getReturnPortal(player);
        if (returnPortal.isPresent()) {
            return returnPortal;
        }

        return getPlayerWaystoneData(player.level()).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level().dimension())
                .min((first, second) -> {
                    double firstDist = player.distanceToSqr(first.getPos().getCenter());
                    double secondDist = player.distanceToSqr(second.getPos().getCenter());
                    return Double.compare(firstDist, secondDist);
                });
    }

    public static Collection<Waystone> getActivatedWaystones(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player);
    }

    public static Optional<? extends Waystone> findWaystone(ServerPlayer player, UUID waystoneUid) {
        final var menuWaystone = findWaystoneInMenu(player, waystoneUid);
        if (menuWaystone.isPresent()) {
            return menuWaystone;
        }

        final var waystone = new WaystoneProxy(player.level().getServer(), waystoneUid);
        if (waystone.isValid()) {
            return Optional.of(waystone);
        }

        return TwinboundFeatherTargets.findTarget(player, waystoneUid);
    }

    private static Optional<? extends Waystone> findWaystoneInMenu(ServerPlayer player, UUID waystoneUid) {
        if (player.containerMenu instanceof WaystoneSelectionMenu selectionMenu) {
            final var fromWaystone = selectionMenu.getWaystoneFrom();
            if (fromWaystone != null && fromWaystone.getWaystoneUid().equals(waystoneUid)) {
                return Optional.of(fromWaystone);
            }

            return selectionMenu.getWaystones().stream()
                    .filter(it -> it.getWaystoneUid().equals(waystoneUid))
                    .findFirst()
                    .map(it -> (Waystone) it);
        }

        if (player.containerMenu instanceof WaystoneEditMenu editMenu) {
            final var editWaystone = editMenu.getWaystone();
            if (editWaystone.getWaystoneUid().equals(waystoneUid)) {
                return Optional.of(editWaystone);
            }
        }

        return Optional.empty();
    }

    public static Optional<String> getOwnerUsername(Waystone waystone, @Nullable MinecraftServer server) {
        final var ownerUsername = waystone.getOwnerUsername();
        if (ownerUsername != null) {
            return Optional.of(ownerUsername);
        }

        if (server == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(waystone.getOwnerUid())
                .flatMap(ownerUid -> server.getProfileCache().get(ownerUid))
                .map(GameProfile::getName);
    }

    public static Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid) {
        return getPlayerWaystoneData(player.level()).getWaystoneAlias(player, waystoneUid);
    }

    public static void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias) {
        getPlayerWaystoneData(player.level()).setWaystoneAlias(player, waystoneUid, alias);
    }

    public static Set<ResourceLocation> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        return getPlayerWaystoneData(player.level()).getConfiguredWaystoneGroups(player, waystoneUid);
    }

    public static void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<ResourceLocation> groupIds) {
        if (groupIds.contains(WaystoneGroups.FAVORITES.identifier())) {
            ensureWaystoneGroups(player, List.of(WaystoneGroups.FAVORITES));
        }
        getPlayerWaystoneData(player.level()).setConfiguredWaystoneGroups(player, waystoneUid, groupIds);
    }

    public static boolean isWaystoneHidden(Player player, Waystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneHidden(player, waystone.getWaystoneUid());
    }

    public static void setWaystoneHidden(Player player, UUID waystoneUid, boolean hidden) {
        getPlayerWaystoneData(player.level()).setWaystoneHidden(player, waystoneUid, hidden);
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

    public static PersonalizedWaystoneImpl getPlayerDecoratedWaystone(Player player, Waystone waystone) {
        if (waystone instanceof PersonalizedWaystoneImpl personalizedWaystone) {
            return personalizedWaystone;
        }

        final var backingWaystone = waystone instanceof PersonalizedWaystoneImpl personalizedWaystone ? personalizedWaystone.getBackingWaystone() : waystone;
        final var alias = getWaystoneAlias(player, backingWaystone.getWaystoneUid());
        final var configuredWaystoneGroups = getConfiguredWaystoneGroups(player, backingWaystone.getWaystoneUid());
        final var hidden = getPlayerWaystoneData(player.level()).isWaystoneHidden(player, backingWaystone.getWaystoneUid());
        return new PersonalizedWaystoneImpl(backingWaystone, alias.orElse(null), configuredWaystoneGroups, hidden);
    }

    public static List<PersonalizedWaystoneImpl> getPlayerDecoratedWaystones(Player player, Collection<Waystone> waystones) {
        return waystones.stream()
                .map(waystone -> getPlayerDecoratedWaystone(player, waystone))
                .toList();
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(BalmEnvironment side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static List<UUID> getSortingIndex(Player player) {
        return getPlayerWaystoneData(player.level()).getSortingIndex(player);
    }

    public static List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones) {
        return getPlayerWaystoneData(player.level()).ensureSortingIndex(player, waystones);
    }

    public static WaystoneSortMode getWaystoneSortMode(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystoneSortMode(player);
    }

    public static void setWaystoneSortMode(Player player, WaystoneSortMode sortMode) {
        getPlayerWaystoneData(player.level()).setWaystoneSortMode(player, sortMode);
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

    public static void sortWaystoneGroupAsFirst(Player player, ResourceLocation groupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupAsFirst(player, groupId);
    }

    public static void sortWaystoneGroupAsLast(Player player, ResourceLocation groupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupAsLast(player, groupId);
    }

    public static void sortWaystoneGroupSwap(Player player, ResourceLocation groupId, ResourceLocation otherGroupId) {
        getPlayerWaystoneData(player.level()).sortWaystoneGroupSwap(player, groupId, otherGroupId);
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

    public static Collection<Waystone> getTargetsForPlayer(ServerPlayer player) {
        final var result = new ArrayList<>(PlayerWaystoneManager.getActivatedWaystones(player));
        addThirdPartyWaystones(player, result);
        result.addAll(FleetingMemorialManager.getTargets(player));
        return result;
    }

    public static Collection<Waystone> getTargetsForItem(ServerPlayer player, ItemStack itemStack) {
        final var result = new ArrayList<>(PlayerWaystoneManager.getTargetsForPlayer(player));
        result.addAll(TwinboundFeatherTargets.getTargets(player));
        WarpPortalManager.getReturnPortal(player).ifPresent(result::add);
        return result;
    }

    public static Collection<Waystone> getTargetsForWaystone(ServerPlayer player, Waystone waystone) {
        final var result = getTargetsForWaystoneType(player, waystone.getWaystoneType());

        final var blockEntity = player.level().getBlockEntity(waystone.getPos());
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            result.addAll(waystoneBlockEntity.getAuxiliaryTargets());
        }
        result.addAll(FleetingMemorialManager.getTargets(player));
        // Twins are only available in Waystone-target teleports
        if (WaystoneTypes.WAYSTONE.equals(waystone.getWaystoneType())) {
            result.addAll(TwinboundFeatherTargets.getTargets(player));
        }
        WarpPortalManager.getReturnPortal(player).ifPresent(result::add);

        return result;
    }

    public static Collection<Waystone> getTargetsForWaystoneType(ServerPlayer player, ResourceLocation waystoneType) {
        final var result = new ArrayList<Waystone>();
        if (WaystoneTypes.isSharestone(waystoneType)) {
            result.addAll(WaystoneManagerImpl.get(player.getServer()).getWaystonesByType(waystoneType).toList());
        } else {
            result.addAll(PlayerWaystoneManager.getActivatedWaystones(player));
            addThirdPartyWaystones(player, result);
        }

        return result;
    }

    public static Collection<Waystone> getTargetsForInventoryButton(ServerPlayer player) {
        final var result = new ArrayList<>(PlayerWaystoneManager.getTargetsForPlayer(player));
        result.addAll(TwinboundFeatherTargets.getTargets(player));
        return result;
    }

    private static void addThirdPartyWaystones(ServerPlayer player, Collection<Waystone> result) {
        // Keep track of ones we're already listing from other sources to avoid duplicates below
        final var knownWaystoneIds = new HashSet<UUID>();
        for (final var waystone : result) {
            knownWaystoneIds.add(waystone.getWaystoneUid());
        }

        for (final var waystone : WaystoneIndexManager.getTargets(player)) {
            if (knownWaystoneIds.add(waystone.getWaystoneUid())) {
                result.add(waystone);
            }
        }
    }
}
