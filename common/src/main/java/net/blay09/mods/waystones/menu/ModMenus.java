package net.blay09.mods.waystones.menu;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.world.BalmMenuFactory;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModMenus {

    public record ItemInitiatedWaystoneMenuData(List<UserDecoratedWaystone> waystones, ItemStack itemStack, Map<UUID, Either<List<Object>, List<Object>>> warpRequirements) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemInitiatedWaystoneMenuData> STREAM_CODEC = StreamCodec.composite(
                UserDecoratedWaystone.LIST_STREAM_CODEC,
                ItemInitiatedWaystoneMenuData::waystones,
                ItemStack.STREAM_CODEC,
                ItemInitiatedWaystoneMenuData::itemStack,
                WaystoneSelectionMenu.WARP_REQUIREMENTS_STREAM_CODEC,
                ItemInitiatedWaystoneMenuData::warpRequirements,
                ItemInitiatedWaystoneMenuData::new
        );
    }

    public record WaystoneListMenuData(List<UserDecoratedWaystone> waystones, Map<UUID, Either<List<Object>, List<Object>>> warpRequirements) {
        public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneListMenuData> STREAM_CODEC = StreamCodec.composite(
                UserDecoratedWaystone.LIST_STREAM_CODEC,
                WaystoneListMenuData::waystones,
                WaystoneSelectionMenu.WARP_REQUIREMENTS_STREAM_CODEC,
                WaystoneListMenuData::warpRequirements,
                WaystoneListMenuData::new
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
    public static Holder<MenuType<PersonalWaystoneSettingsMenu>> personalWaystoneSettings;

    public static void initialize(BalmMenuTypeRegistrar menus) {
        waystoneSelection = menus.register("waystone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                        return new WaystoneSelectionMenu(ModMenus.waystoneSelection.value(),
                                data.fromWaystone(),
                                windowId,
                                data.waystones(),
                                data.warpRequirements(),
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
                        return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.value(), null, windowId, data.waystones(), data.warpRequirements(), Collections.emptySet())
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
                        return new WaystoneSelectionMenu(ModMenus.warpStoneSelection.value(), null, windowId, data.waystones(), data.warpRequirements(), Collections.emptySet())
                                .withWarpItem(data.itemStack());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, ItemInitiatedWaystoneMenuData> getStreamCodec() {
                        return ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                    }
                }).asHolder();
        portstoneSelection = menus.register("portstone_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneListMenuData>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneListMenuData data) {
                        return new WaystoneSelectionMenu(ModMenus.portstoneSelection.value(), null, windowId, data.waystones(), data.warpRequirements(), Set.of(TeleportFlags.PORTSTONE));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneListMenuData> getStreamCodec() {
                        return WaystoneListMenuData.STREAM_CODEC;
                    }
                }).asHolder();
        inventorySelection = menus.register("inventory_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneListMenuData>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneListMenuData data) {
                        return new WaystoneSelectionMenu(ModMenus.inventorySelection.value(), null, windowId, data.waystones(), data.warpRequirements(), Set.of(TeleportFlags.INVENTORY_BUTTON));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneListMenuData> getStreamCodec() {
                        return WaystoneListMenuData.STREAM_CODEC;
                    }
                }).asHolder();
        adminSelection = menus.register("admin_selection",
                new BalmMenuFactory<WaystoneSelectionMenu, WaystoneListMenuData>() {
                    @Override
                    public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneListMenuData data) {
                        return new WaystoneSelectionMenu(ModMenus.adminSelection.value(), null, windowId, data.waystones(), data.warpRequirements(), Set.of(TeleportFlags.ADMIN));
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneListMenuData> getStreamCodec() {
                        return WaystoneListMenuData.STREAM_CODEC;
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
                                data.warpRequirements(),
                                Collections.emptySet());
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                        return WaystoneSelectionMenu.STREAM_CODEC;
                    }
                }).asHolder();
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

}
