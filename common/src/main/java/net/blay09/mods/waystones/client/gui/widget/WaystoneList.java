package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.ServerboundSelectWaystonePacket;
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
            waystoneButton = new WaystoneButton(0,
                    0,
                    getRowWidth(),
                    waystone,
                    menu.getWarpRequirements(waystone),
                    _ -> Balm.networking().sendToServer(new ServerboundSelectWaystonePacket(waystone.getWaystoneUid())));
            final var waystoneFrom = menu.getWaystoneFrom();
            if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
                waystoneButton.active = false;
            }
            widgets = List.of(waystoneButton);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            if (waystoneButton != null) {
                waystoneButton.setPosition(x, getY() + 1);
            }
        }

        @Override
        public void setY(int y) {
            super.setY(y);
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
