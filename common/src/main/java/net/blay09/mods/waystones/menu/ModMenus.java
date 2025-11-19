package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.world.BalmMenuFactory;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ModMenus {

    public record ItemInitiatedWaystoneMenuData(List<Waystone> waystones, ItemStack itemStack) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemInitiatedWaystoneMenuData> STREAM_CODEC = StreamCodec.composite(
                WaystoneImpl.LIST_STREAM_CODEC,
                ItemInitiatedWaystoneMenuData::waystones,
                ItemStack.STREAM_CODEC,
                ItemInitiatedWaystoneMenuData::itemStack,
                ItemInitiatedWaystoneMenuData::new
        );
    }

    public static Holder<MenuType<WaystoneSelectionMenu>> waystoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> warpScrollSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> warpStoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> portstoneSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> inventorySelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> adminSelection;
    public static Holder<MenuType<WaystoneSelectionMenu>> sharestoneSelection;
    public static Holder<MenuType<WaystoneModifierMenu>> waystoneModifiers;
    public static Holder<MenuType<WaystoneEditMenu>> waystoneSettings;

    public static void initialize(BalmMenuTypeRegistrar menus) {
        waystoneSelection = menus.register("waystone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                        return new WaystoneSelectionMenu(ModMenus.waystoneSelection.value(),
                                data.fromWaystone(),
                                windowId,
                                data.waystones(),
                                Collections.emptySet());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                        return WaystoneSelectionMenu.STREAM_CODEC;
                    }
                }).asHolder();
        warpScrollSelection = menus.register("warp_scroll_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, ItemInitiatedWaystoneMenuData>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, ItemInitiatedWaystoneMenuData data) {
                        return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.value(), null, windowId, data.waystones(), Collections.emptySet())
                                .withWarpItem(data.itemStack());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, ItemInitiatedWaystoneMenuData> getStreamCodec() {
                        return ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                    }
                }).asHolder();
        warpStoneSelection = menus.register("warp_stone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, ItemInitiatedWaystoneMenuData>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, ItemInitiatedWaystoneMenuData data) {
                        return new WaystoneSelectionMenu(ModMenus.warpStoneSelection.value(), null, windowId, data.waystones(), Collections.emptySet())
                                .withWarpItem(data.itemStack());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, ItemInitiatedWaystoneMenuData> getStreamCodec() {
                        return ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                    }
                }).asHolder();
        portstoneSelection = menus.register("portstone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, List<Waystone>>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, List<Waystone> waystones) {
                        return new WaystoneSelectionMenu(ModMenus.portstoneSelection.value(), null, windowId, waystones, Set.of(TeleportFlags.PORTSTONE));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, List<Waystone>> getStreamCodec() {
                        return WaystoneImpl.LIST_STREAM_CODEC;
                    }
                }).asHolder();
        inventorySelection = menus.register("inventory_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, List<Waystone>>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, List<Waystone> waystones) {
                        return new WaystoneSelectionMenu(ModMenus.inventorySelection.value(), null, windowId, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, List<Waystone>> getStreamCodec() {
                        return WaystoneImpl.LIST_STREAM_CODEC;
                    }
                }).asHolder();
        adminSelection = menus.register("admin_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, List<Waystone>>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, List<Waystone> waystones) {
                        return new WaystoneSelectionMenu(ModMenus.adminSelection.value(), null, windowId, waystones, Set.of(TeleportFlags.ADMIN));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, List<Waystone>> getStreamCodec() {
                        return WaystoneImpl.LIST_STREAM_CODEC;
                    }
                }).asHolder();
        sharestoneSelection = menus.register("sharestone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                        return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.value(),
                                data.fromWaystone(),
                                windowId,
                                data.waystones(),
                                Collections.emptySet());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                        return WaystoneSelectionMenu.STREAM_CODEC;
                    }
                }).asHolder();
        waystoneModifiers = menus.register("waystone_modifiers",
                new BalmMenuFactory<WaystoneModifierMenu, Waystone>() {
                    @Override
                    public WaystoneModifierMenu create(int windowId, Inventory inventory, Waystone waystone) {
                        return new WaystoneModifierMenu(windowId, inventory, waystone);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, Waystone> getStreamCodec() {
                        return WaystoneImpl.STREAM_CODEC;
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
    }

}
