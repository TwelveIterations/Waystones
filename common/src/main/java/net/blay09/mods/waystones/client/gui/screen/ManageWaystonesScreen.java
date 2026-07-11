package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.client.gui.widget.AbstractWaystoneList;
import net.blay09.mods.waystones.client.gui.widget.BackToWaystoneSelectionButton;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystoneGroupsButton;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystonesList;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.RemoveWaystoneMessage;
import net.blay09.mods.waystones.network.message.SortWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.Objects;

public class ManageWaystonesScreen extends WaystoneSelectionScreenBase {

    private final WaystoneSelectionScreenBase parent;
    private final Inventory playerInventory;

    public ManageWaystonesScreen(WaystoneSelectionMenu menu, Inventory playerInventory, WaystoneSelectionScreenBase parent) {
        super(menu, playerInventory, Component.translatable("container.waystones.manage_waystones"));
        this.parent = parent;
        this.playerInventory = playerInventory;
    }

    @Override
    protected AbstractWaystoneList<?> createWaystoneList() {
        return new ManageWaystonesList(leftPos,
                topPos + HEADER_HEIGHT,
                imageWidth,
                imageHeight - HEADER_HEIGHT - FOOTER_HEIGHT,
                this);
    }

    @Override
    protected boolean shouldShowWaystone(Waystone waystone) {
        return isManageable(waystone);
    }

    private static boolean isManageable(Waystone waystone) {
        final var waystoneType = waystone.getWaystoneType();
        return !WaystoneTypes.WARP_PORTAL.equals(waystoneType) && !WaystoneTypes.FLEETING_MEMORIAL.equals(waystoneType);
    }

    public boolean canReorderWaystones() {
        return parent.allowReordering();
    }

    public boolean canDeleteWaystone(Waystone waystone) {
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        final var isCreative = player.getAbilities().instabuild;
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && !isCreative) {
            return false;
        }

        if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            if (!isCreative) {
                return false;
            }
        } else if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            return false;
        }

        return parent.allowDeletion();
    }

    public boolean canEditPersonalWaystoneSettings(Waystone waystone) {
        return true;
    }

    public void deleteWaystone(Waystone waystone) {
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        PlayerWaystoneManager.deactivateWaystone(player, waystone);
        removeWaystoneLocally(waystone);
        parent.removeWaystoneLocally(waystone);
        Balm.getNetworking().sendToServer(new RemoveWaystoneMessage(waystone.getWaystoneUid()));
    }

    public void openPersonalWaystoneSettings(Waystone waystone) {
        final var personalizedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(playerInventory.player, waystone);
        openSiblingScreen(new PersonalWaystoneSettingsScreen(menu, playerInventory, personalizedWaystone, this));
    }

    public void reorderWaystone(Waystone waystone, Waystone otherWaystone) {
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        final var waystoneUid = waystone.getWaystoneUid();
        final var otherWaystoneUid = otherWaystone.getWaystoneUid();
        final int index = filteredWaystones.indexOf(waystone);
        final int otherIndex = filteredWaystones.indexOf(otherWaystone);
        if (index == -1 || otherIndex == -1) {
            return;
        }

        PlayerWaystoneManager.sortWaystoneSwap(player, waystoneUid, otherWaystoneUid);
        Balm.getNetworking().sendToServer(new SortWaystoneMessage(waystoneUid, otherWaystoneUid));
        Collections.swap(filteredWaystones, index, otherIndex);
    }

    public void moveWaystoneToTop(Waystone waystone) {
        final int index = filteredWaystones.indexOf(waystone);
        if (index == -1) {
            return;
        }

        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        final var waystoneUid = waystone.getWaystoneUid();
        filteredWaystones.remove(index);
        filteredWaystones.addFirst(waystone);
        PlayerWaystoneManager.sortWaystoneAsFirst(player, waystoneUid);
        Balm.getNetworking().sendToServer(new SortWaystoneMessage(waystoneUid, SortWaystoneMessage.SORT_FIRST));
    }

    public void moveWaystoneToBottom(Waystone waystone) {
        final int index = filteredWaystones.indexOf(waystone);
        if (index == -1) {
            return;
        }

        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        final var waystoneUid = waystone.getWaystoneUid();
        filteredWaystones.remove(index);
        filteredWaystones.add(waystone);
        PlayerWaystoneManager.sortWaystoneAsLast(player, waystoneUid);
        Balm.getNetworking().sendToServer(new SortWaystoneMessage(waystoneUid, SortWaystoneMessage.SORT_LAST));
    }

    @Override
    protected void initSideButtons() {
        final var backButton = new BackToWaystoneSelectionButton(leftPos - 8,
                topPos + HEADER_HEIGHT - 24,
                button -> returnToSelection());
        addRenderableWidget(backButton);

        final var manageGroupsButton = new ManageWaystoneGroupsButton(leftPos - 8,
                topPos + HEADER_HEIGHT,
                button -> openSiblingScreen(new ManageWaystoneGroupsScreen(menu, playerInventory, this)));
        addRenderableWidget(manageGroupsButton);
    }

    @Override
    protected boolean allowSorting() {
        return false;
    }

    @Override
    public void onClose() {
        returnToSelection();
    }

    private void returnToSelection() {
        openSiblingScreen(parent);
    }
}
