package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
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

    public static MenuProvider getProvider(Player player, Waystone waystone) {
        final var decoratedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, waystone);
        return new BalmMenuProvider<UserDecoratedWaystone>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.personal_waystone_settings", waystone.getName());
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                return new PersonalWaystoneSettingsMenu(i, decoratedWaystone);
            }

            @Override
            public UserDecoratedWaystone getScreenOpeningData(ServerPlayer serverPlayer) {
                return decoratedWaystone;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> getScreenStreamCodec() {
                return UserDecoratedWaystone.STREAM_CODEC;
            }
        };
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
