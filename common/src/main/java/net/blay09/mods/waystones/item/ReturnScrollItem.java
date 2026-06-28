package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpPortalManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ReturnScrollItem extends BoundScrollItem {

    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        return WarpPortalManager.getReturnPortal(player).or(() -> PlayerWaystoneManager.getNearestWaystone(player));
    }
}
