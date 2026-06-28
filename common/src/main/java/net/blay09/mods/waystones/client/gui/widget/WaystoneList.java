package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.SelectWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class WaystoneList extends AbstractWaystoneList<WaystoneList.WaystoneEntry> {

    private final WaystoneSelectionMenu menu;

    public WaystoneList(int x, int y, int width, int height, WaystoneSelectionMenu menu) {
        super(x, y, width, height);
        this.menu = menu;
    }

    @Override
    protected WaystoneEntry createEntry(Waystone waystone, int index, int waystoneCount) {
        return new WaystoneEntry(waystone);
    }

    public class WaystoneEntry extends Entry<WaystoneEntry> {

        private final WaystoneButton waystoneButton;
        private final List<AbstractWidget> widgets;

        public WaystoneEntry(Waystone waystone) {
            final var waystoneFrom = menu.getWaystoneFrom();
            final var player = Minecraft.getInstance().player;
            final var context = WaystonesAPI.createUnboundTeleportContext(player, waystone)
                    .setFromWaystone(waystoneFrom)
                    .setWarpItem(menu.getWarpItem())
                    .addFlags(menu.getFlags());
            final var requirements = WaystonesAPI.resolveRequirements(context);
            waystoneButton = new WaystoneButton(0,
                    0,
                    getRowWidth(),
                    waystone,
                    requirements,
                    button -> Balm.getNetworking().sendToServer(new SelectWaystoneMessage(waystone.getWaystoneUid())));
            if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
                waystoneButton.active = false;
            }
            widgets = List.of(waystoneButton);
        }

        @Override
        protected void updateWidgetPositions() {
            if (waystoneButton != null) {
                waystoneButton.setPosition(getX(), y + 1);
            }
        }

        @Override
        protected List<AbstractWidget> widgets() {
            return widgets;
        }
    }
}
