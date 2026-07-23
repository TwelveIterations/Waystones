package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.TwinboundFeatherLinksComponent;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TwinboundFeatherTargets {

    private static final ConcurrentHashMap<UUID, UUID> lastSeenOwners = new ConcurrentHashMap<>();

    private TwinboundFeatherTargets() {
    }

    public static void markLastSeen(ItemStack itemStack, ServerPlayer player) {
        final var featherId = itemStack.get(ModComponents.twinboundFeather.value());
        if (featherId != null) {
            lastSeenOwners.put(featherId, player.getUUID());
        }
    }

    public static List<Waystone> getTargets(ServerPlayer player) {
        final var result = new LinkedHashMap<UUID, Waystone>();
        for (final var sourceStack : findCarriedTwinboundFeathers(player)) {
            final var sourceFeatherId = sourceStack.get(ModComponents.twinboundFeather.value());
            if (sourceFeatherId == null) {
                continue;
            }

            final var sourceLinks = sourceStack.getOrDefault(ModComponents.twinboundFeatherLinks.value(), TwinboundFeatherLinksComponent.EMPTY);
            if (sourceLinks.links().isEmpty()) {
                continue;
            }

            final var server = player.level().getServer();
            for (final var targetFeatherId : sourceLinks.links()) {
                final var targetPlayerId = lastSeenOwners.get(targetFeatherId);
                if (targetPlayerId == null) {
                    continue;
                }

                final var targetPlayer = server.getPlayerList().getPlayer(targetPlayerId);
                if (targetPlayer == null) {
                    lastSeenOwners.remove(targetFeatherId, targetPlayerId);
                    continue;
                }

                final var targetStack = findCarriedTwinboundFeather(targetPlayer, targetFeatherId);
                if (targetStack.isEmpty()) {
                    lastSeenOwners.remove(targetFeatherId, targetPlayerId);
                    continue;
                }

                final var targetLinks = targetStack.get().getOrDefault(ModComponents.twinboundFeatherLinks.value(), TwinboundFeatherLinksComponent.EMPTY);
                if (targetLinks.links().contains(sourceFeatherId)) {
                    final var waystone = createTargetWaystone(targetPlayer);
                    result.putIfAbsent(waystone.getWaystoneUid(), waystone);
                }
            }
        }
        return List.copyOf(result.values());
    }

    public static Optional<Waystone> findTarget(ServerPlayer player, UUID targetWaystoneUid) {
        return getTargets(player).stream()
                .filter(it -> it.getWaystoneUid().equals(targetWaystoneUid))
                .findFirst();
    }

    private static Waystone createTargetWaystone(ServerPlayer targetPlayer) {
        final var uid = targetPlayer.getUUID();
        final var name = Component.translatable("gui.waystones.waystone_selection.twinbound_feather", targetPlayer.getDisplayName());
        return new TwinboundFeatherWaystone(uid, targetPlayer.level().dimension(), targetPlayer.blockPosition(), name);
    }

    public static Optional<ItemStack> findCarriedTwinboundFeather(Player player) {
        return findCarriedTwinboundFeathers(player).stream().findFirst();
    }

    private static Optional<ItemStack> findCarriedTwinboundFeather(Player player, UUID featherId) {
        return findCarriedTwinboundFeathers(player).stream()
                .filter(it -> featherId.equals(it.get(ModComponents.twinboundFeather.value())))
                .findFirst();
    }

    private static List<ItemStack> findCarriedTwinboundFeathers(Player player) {
        final var result = new ArrayList<ItemStack>();
        final var seen = Collections.newSetFromMap(new IdentityHashMap<ItemStack, Boolean>());
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            addIfTwinboundFeather(player.getInventory().getItem(i), result, seen);
        }
        addIfTwinboundFeather(player.getOffhandItem(), result, seen);
        for (final var itemStack : Balm.modSupport().trinkets().findAllEquipped(player, TwinboundFeatherTargets::isTwinboundFeather)) {
            addIfTwinboundFeather(itemStack, result, seen);
        }
        return result;
    }

    private static void addIfTwinboundFeather(ItemStack itemStack, List<ItemStack> result, Set<ItemStack> seen) {
        if (isTwinboundFeather(itemStack) && seen.add(itemStack)) {
            result.add(itemStack);
        }
    }

    private static boolean isTwinboundFeather(ItemStack itemStack) {
        return itemStack.is(ModItems.twinboundFeather.asItem());
    }
}
