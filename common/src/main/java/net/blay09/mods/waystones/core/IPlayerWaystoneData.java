package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IPlayerWaystoneData {
    void activateWaystone(Player player, IWaystone waystone);
    boolean isWaystoneActivated(Player player, IWaystone waystone);
    void deactivateWaystone(Player player, IWaystone waystone);
    long getWarpStoneCooldownUntil(Player player);
    void setWarpStoneCooldownUntil(Player player, long timeStamp);
    long getInventoryButtonCooldownUntil(Player player);
    void setInventoryButtonCooldownUntil(Player player, long timeStamp);
    List<IWaystone> getWaystones(Player player);
    void swapWaystoneSorting(Player player, int index, int otherIndex);
}
