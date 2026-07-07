package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.world.BalmMenuFactory;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class ModMenus {

    public static Holder<MenuType<WaystoneSelectionMenu>> waystoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> warpScrollSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> portalScrollSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> warpStoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> portstoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> inventorySelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> adminSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> sharestoneSelection;
    public static Holder<MenuType<WaystoneModifierMenu>> waystoneModifiers;
    public static Holder<MenuType<WaystoneEditMenu>> waystoneSettings;
    public static Holder<MenuType<PersonalWaystoneSettingsMenu>> personalWaystoneSettings;

    public static void initialize(BalmMenuTypeRegistrar menus) {
        waystoneSelection = registerWaystoneSelectionMenu(menus, "waystone_selection", () -> ModMenus.waystoneSelection, Collections.emptySet());
        warpScrollSelection = registerWaystoneSelectionMenu(menus, "warp_scroll_selection", () -> ModMenus.warpScrollSelection, Collections.emptySet());
        portalScrollSelection = registerWaystoneSelectionMenu(menus, "portal_scroll_selection", () -> ModMenus.portalScrollSelection, Collections.emptySet());
        warpStoneSelection = registerWaystoneSelectionMenu(menus, "warp_stone_selection", () -> ModMenus.warpStoneSelection, Collections.emptySet());
        portstoneSelection = registerWaystoneSelectionMenu(menus, "portstone_selection", () -> ModMenus.portstoneSelection, Set.of(TeleportFlags.PORTSTONE));
        inventorySelection = registerWaystoneSelectionMenu(menus, "inventory_selection", () -> ModMenus.inventorySelection, Set.of(TeleportFlags.INVENTORY_BUTTON));
        adminSelection = registerWaystoneSelectionMenu(menus, "admin_selection", () -> ModMenus.adminSelection, Set.of(TeleportFlags.ADMIN));
        sharestoneSelection = registerWaystoneSelectionMenu(menus, "sharestone_selection", () -> ModMenus.sharestoneSelection, Collections.emptySet());
        waystoneModifiers = menus.register("waystone_modifiers",
                new BalmMenuFactory<WaystoneModifierMenu, UserDecoratedWaystone>() {
                    @Override
                    public WaystoneModifierMenu create(int windowId, Inventory inventory, UserDecoratedWaystone waystone) {
                        return new WaystoneModifierMenu(windowId, inventory, waystone);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> getStreamCodec() {
                        return UserDecoratedWaystone.STREAM_CODEC;
                    }
                }).asHolder();
        waystoneSettings = menus.register("waystone",
                new BalmMenuFactory<WaystoneEditMenu, WaystoneEditMenu.Data>() {
                    @Override
                    public WaystoneEditMenu create(int windowId, Inventory inventory, WaystoneEditMenu.Data data) {
                        return new WaystoneEditMenu(windowId, data.waystone(), data.modifierCount(), data.error().orElse(null), data.visibilityOptions());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> getStreamCodec() {
                        return WaystoneEditMenu.STREAM_CODEC;
                    }
                }).asHolder();
        personalWaystoneSettings = menus.register("personal_waystone_settings",
                new BalmMenuFactory<PersonalWaystoneSettingsMenu, UserDecoratedWaystone>() {
                    @Override
                    public PersonalWaystoneSettingsMenu create(int windowId, Inventory inventory, UserDecoratedWaystone waystone) {
                        return new PersonalWaystoneSettingsMenu(windowId, waystone);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> getStreamCodec() {
                        return UserDecoratedWaystone.STREAM_CODEC;
                    }
                }).asHolder();
    }

    private static Holder<MenuType<WaystoneSelectionMenu>> registerWaystoneSelectionMenu(BalmMenuTypeRegistrar menus, String name, Supplier<Holder<MenuType<WaystoneSelectionMenu>>> menuType, Set<Identifier> flags) {
        return menus.register(name,
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                        return new WaystoneSelectionMenu(menuType.get().value(),
                                data.fromWaystone(),
                                windowId,
                                data.waystones(),
                                data.warpRequirements(),
                                flags);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                        return WaystoneSelectionMenu.STREAM_CODEC;
                    }
                }).asHolder();
    }

}
