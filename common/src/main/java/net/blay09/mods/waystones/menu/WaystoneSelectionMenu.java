package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSelectionMenu extends AbstractContainerMenu {

    private final WarpMode warpMode;
    private final IWaystone fromWaystone;
    private final List<IWaystone> waystones;

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, WarpMode warpMode, @Nullable IWaystone fromWaystone, int windowId, List<IWaystone> waystones) {
        super(type, windowId);
        this.warpMode = warpMode;
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
        }

        return true;
    }

    @Nullable
    public IWaystone getWaystoneFrom() {
        return fromWaystone;
    }

    public WarpMode getWarpMode() {
        return warpMode;
    }

    public List<IWaystone> getWaystones() {
        return waystones;
    }

    public static WaystoneSelectionMenu createWaystoneSelection(int windowId, Player player, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(), warpMode, fromWaystone, windowId, waystones);
    }

    public static WaystoneSelectionMenu createSharestoneSelection(MinecraftServer server, int windowId, IWaystone fromWaystone, BlockState state) {
        SharestoneBlock block = (SharestoneBlock) state.getBlock();
        ResourceLocation waystoneType = WaystoneTypes.getSharestone(block.getColor());
        List<IWaystone> waystones = WaystoneManager.get(server).getWaystonesByType(waystoneType).collect(Collectors.toList());
        return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.get(), WarpMode.SHARESTONE_TO_SHARESTONE, fromWaystone, windowId, waystones);
    }

}
