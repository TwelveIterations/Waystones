package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ReturnScrollItem extends BoundScrollItem {

    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected IWaystone getBoundTo(Player player, ItemStack itemStack) {
        return PlayerWaystoneManager.getNearestWaystone(player);
    }

    @Override
    protected WarpMode getWarpMode() {
        return WarpMode.RETURN_SCROLL;
    }
}
