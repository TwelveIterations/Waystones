
package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.nbt.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class PersistentPlayerWaystoneData implements IPlayerWaystoneData {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String INVENTORY_BUTTON_COOLDOWN_UNTIL = "InventoryButtonCooldownUntilUnix";
    private static final String WARP_STONE_COOLDOWN_UNTIL = "WarpStoneCooldownUntilUnix";

    @Override
    public void activateWaystone(Player player, IWaystone waystone) {
        ListTag activatedWaystonesData = getActivatedWaystonesData(getWaystonesData(player));
        activatedWaystonesData.add(StringTag.valueOf(waystone.getWaystoneUid().toString()));
    }

    @Override
    public boolean isWaystoneActivated(Player player, IWaystone waystone) {
        ListTag activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (Tag activatedWaystone : activatedWaystones) {
            if (waystoneUid.equals(activatedWaystone.getAsString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<IWaystone> getWaystones(Player player) {
        ListTag activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        List<IWaystone> waystones = new ArrayList<>();
        for (Iterator<Tag> iterator = activatedWaystones.iterator(); iterator.hasNext(); ) {
            Tag activatedWaystone = iterator.next();
            WaystoneProxy proxy = new WaystoneProxy(player.getServer(), UUID.fromString(activatedWaystone.getAsString()));
            if (proxy.isValid()) {
                waystones.add(proxy);
            } else {
                iterator.remove();
            }
        }

        return waystones;
    }

    @Override
    public void swapWaystoneSorting(Player player, int index, int otherIndex) {
        ListTag activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        if (otherIndex == -1) {
            Tag waystone = activatedWaystones.remove(index);
            activatedWaystones.add(0, waystone);
        } else if (otherIndex == activatedWaystones.size()) {
            Tag waystone = activatedWaystones.remove(index);
            activatedWaystones.add(waystone);
        } else {
            Collections.swap(activatedWaystones, index, otherIndex);
        }
    }

    @Override
    public void deactivateWaystone(Player player, IWaystone waystone) {
        CompoundTag data = getWaystonesData(player);
        ListTag activatedWaystones = getActivatedWaystonesData(data);
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (int i = activatedWaystones.size() - 1; i >= 0; i--) {
            Tag activatedWaystone = activatedWaystones.get(i);
            if (waystoneUid.equals(activatedWaystone.getAsString())) {
                activatedWaystones.remove(i);
                break;
            }
        }
    }

    @Override
    public long getWarpStoneCooldownUntil(Player player) {
        return getWaystonesData(player).getLong(WARP_STONE_COOLDOWN_UNTIL);
    }

    @Override
    public void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        getWaystonesData(player).putLong(WARP_STONE_COOLDOWN_UNTIL, timeStamp);
    }

    @Override
    public long getInventoryButtonCooldownUntil(Player player) {
        return getWaystonesData(player).getLong(INVENTORY_BUTTON_COOLDOWN_UNTIL);
    }

    @Override
    public void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        getWaystonesData(player).putLong(INVENTORY_BUTTON_COOLDOWN_UNTIL, timeStamp);
    }

    private static ListTag getActivatedWaystonesData(CompoundTag data) {
        ListTag list = data.getList(ACTIVATED_WAYSTONES, Tag.TAG_STRING);
        data.put(ACTIVATED_WAYSTONES, list);
        return list;
    }

    private static CompoundTag getWaystonesData(Player player) {
        CompoundTag persistedData = Balm.getHooks().getPersistentData(player);
        CompoundTag compound = persistedData.getCompound(TAG_NAME);
        persistedData.put(TAG_NAME, compound);
        return compound;
    }
}
