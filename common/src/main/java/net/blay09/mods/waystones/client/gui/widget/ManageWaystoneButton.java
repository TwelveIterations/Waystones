package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ManageWaystoneButton extends Button.Plain {

    public ManageWaystoneButton(int width, Waystone waystone) {
        super(0, 0, width, 20, getWaystoneName(waystone), _ -> {
        }, Button.DEFAULT_NARRATION);
        active = false;
    }

    private static Component getWaystoneName(Waystone waystone) {
        var effectiveName = waystone.getName().copy();
        if (effectiveName.getString().isEmpty()) {
            effectiveName = Component.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        }
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)) {
            effectiveName.withStyle(ChatFormatting.YELLOW);
        }
        return effectiveName;
    }
}
