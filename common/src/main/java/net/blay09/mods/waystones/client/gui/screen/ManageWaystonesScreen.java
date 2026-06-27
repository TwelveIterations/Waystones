package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.client.gui.widget.AbstractWaystoneList;
import net.blay09.mods.waystones.client.gui.widget.BackToWaystoneSelectionButton;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystoneGroupsButton;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystonesList;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.ServerboundRequestPersonalWaystoneSettingsPacket;
import net.blay09.mods.waystones.network.message.ServerboundRemoveWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundSortWaystonePacket;
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
        return new ManageWaystonesList(leftPos + (imageWidth - AbstractWaystoneList.ENTRY_WIDTH) / 2,
                topPos + HEADER_HEIGHT,
                AbstractWaystoneList.ENTRY_WIDTH,
                imageHeight - HEADER_HEIGHT - FOOTER_HEIGHT,
                this);
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

        if (WaystoneKinds.isSharestone(waystone.getWaystoneKind())) {
            if (!isCreative) {
                return false;
            }
        } else if (!waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)) {
            return false;
        }

        return parent.allowDeletion();
    }

    public void deleteWaystone(Waystone waystone) {
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        PlayerWaystoneManager.deactivateWaystone(player, waystone);
        waystones.remove(waystone);
        Balm.networking().sendToServer(new ServerboundRemoveWaystonePacket(waystone.getWaystoneUid()));
        updateList();
    }

    public void openPersonalWaystoneSettings(Waystone waystone) {
        Balm.networking().sendToServer(new ServerboundRequestPersonalWaystoneSettingsPacket(waystone.getWaystoneUid()));
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
        Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, otherWaystoneUid));
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
        Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, ServerboundSortWaystonePacket.SORT_FIRST));
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
        Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, ServerboundSortWaystonePacket.SORT_LAST));
    }

    @Override
    protected void initSideButtons() {
        final var backButton = new BackToWaystoneSelectionButton(leftPos - 8,
                topPos + HEADER_HEIGHT - 24,
                _ -> returnToSelection());
        addRenderableWidget(backButton);

        final var manageGroupsButton = new ManageWaystoneGroupsButton(leftPos - 8,
                topPos + HEADER_HEIGHT,
                _ -> Minecraft.getInstance().setScreen(new ManageWaystoneGroupsScreen(menu, playerInventory, this)));
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
        Minecraft.getInstance().setScreen(parent);
    }
}
