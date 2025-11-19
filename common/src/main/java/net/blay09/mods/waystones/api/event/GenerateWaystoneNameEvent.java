package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;

// TODO
@Deprecated
public class GenerateWaystoneNameEvent {

    private final Waystone waystone;
    private Component name;

    public GenerateWaystoneNameEvent(Waystone waystone, Component name) {
        this.waystone = waystone;
        this.name = name;
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }
}
