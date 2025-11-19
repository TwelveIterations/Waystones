package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class GenerateWaystoneNameEvent {

    public static final BidirectionalEventMapper<Consumer<GenerateWaystoneNameEvent>> EVENT = Balmstrap.createBoundCustomEvent(GenerateWaystoneNameEvent.class);

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
