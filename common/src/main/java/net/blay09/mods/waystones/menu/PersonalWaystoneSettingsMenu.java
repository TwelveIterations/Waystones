package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.PersonalizedWaystoneImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PersonalWaystoneSettingsMenu extends AbstractContainerMenu {

    private final PersonalizedWaystoneImpl waystone;

    public PersonalWaystoneSettingsMenu(int windowId, PersonalizedWaystoneImpl waystone) {
        super(ModMenus.personalWaystoneSettings.get(), windowId);
        this.waystone = waystone;
    }

    public static MenuProvider getProvider(Player player, Waystone waystone) {
        final var decoratedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, waystone);
        return new BalmMenuProvider<PersonalizedWaystoneImpl>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.personal_waystone_settings", waystone.getName());
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                return new PersonalWaystoneSettingsMenu(i, decoratedWaystone);
            }

            @Override
            public PersonalizedWaystoneImpl getScreenOpeningData(ServerPlayer serverPlayer) {
                return decoratedWaystone;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, PersonalizedWaystoneImpl> getScreenStreamCodec() {
                return PersonalizedWaystoneImpl.STREAM_CODEC;
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

    public Set<ResourceLocation> getConfiguredGroups() {
        return waystone.getConfiguredGroups();
    }
}
