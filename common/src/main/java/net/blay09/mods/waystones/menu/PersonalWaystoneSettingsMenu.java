package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class PersonalWaystoneSettingsMenu extends AbstractContainerMenu {

    private final UserDecoratedWaystone waystone;

    public PersonalWaystoneSettingsMenu(int windowId, UserDecoratedWaystone waystone) {
        super(ModMenus.personalWaystoneSettings.value(), windowId);
        this.waystone = waystone;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public @Nullable Component getAlias() {
        return waystone.getAlias().orElse(null);
    }
}
