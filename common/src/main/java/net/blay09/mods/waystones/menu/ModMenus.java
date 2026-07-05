package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.core.PersonalizedWaystoneImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class ModMenus {

    private static final BalmMenus menus = Balm.getMenus();
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> waystoneSelection = registerWaystoneSelectionMenu("waystone_selection", () -> ModMenus.waystoneSelection.get(), Set.of());
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpScrollSelection = registerWaystoneSelectionMenu("warp_scroll_selection", () -> ModMenus.warpScrollSelection.get(), Set.of());
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> portalScrollSelection = registerWaystoneSelectionMenu("portal_scroll_selection", () -> ModMenus.portalScrollSelection.get(), Set.of());
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpStoneSelection = registerWaystoneSelectionMenu("warp_stone_selection", () -> ModMenus.warpStoneSelection.get(), Set.of());
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> portstoneSelection = registerWaystoneSelectionMenu("portstone_selection", () -> ModMenus.portstoneSelection.get(), Set.of(TeleportFlags.PORTSTONE));
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> inventorySelection = registerWaystoneSelectionMenu("inventory_selection", () -> ModMenus.inventorySelection.get(), Set.of(TeleportFlags.INVENTORY_BUTTON));
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> adminSelection = registerWaystoneSelectionMenu("admin_selection", () -> ModMenus.adminSelection.get(), Set.of(TeleportFlags.ADMIN));
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> sharestoneSelection = registerWaystoneSelectionMenu("sharestone_selection", () -> ModMenus.sharestoneSelection.get(), Set.of());
    public static DeferredObject<MenuType<WaystoneModifierMenu>> waystoneModifiers = menus.registerMenu(id("waystone_modifiers"),
            new BalmMenuFactory<WaystoneModifierMenu, PersonalizedWaystoneImpl>() {
                @Override
                public WaystoneModifierMenu create(int windowId, Inventory inventory, PersonalizedWaystoneImpl waystone) {
                    return new WaystoneModifierMenu(windowId, inventory, waystone);
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, PersonalizedWaystoneImpl> getStreamCodec() {
                    return PersonalizedWaystoneImpl.STREAM_CODEC;
                }
            });

    private static DeferredObject<MenuType<WaystoneSelectionMenu>> registerWaystoneSelectionMenu(String name, Supplier<MenuType<WaystoneSelectionMenu>> menuType, Set<ResourceLocation> flags) {
        return menus.registerMenu(id(name),
            new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
                @Override
                public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                    return new WaystoneSelectionMenu(menuType.get(),
                            data.fromWaystone().orElse(null),
                            windowId,
                            data.waystones(),
                            flags,
                            data.targetKind().orElse(null))
                            .withWarpItem(data.warpItem());
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                    return WaystoneSelectionMenu.STREAM_CODEC;
                }
            });
    }

    public static DeferredObject<MenuType<WaystoneEditMenu>> waystoneSettings = menus.registerMenu(id("waystone"),
            new BalmMenuFactory<WaystoneEditMenu, WaystoneEditMenu.Data>() {
                @Override
                public WaystoneEditMenu create(int windowId, Inventory inventory, WaystoneEditMenu.Data data) {
                    return new WaystoneEditMenu(windowId, data.waystone(), data.modifierCount(), data.error().orElse(null), data.visibilityOptions());
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> getStreamCodec() {
                    return WaystoneEditMenu.STREAM_CODEC;
                }
            });
    public static DeferredObject<MenuType<PersonalWaystoneSettingsMenu>> personalWaystoneSettings = menus.registerMenu(id("personal_waystone_settings"),
            new BalmMenuFactory<PersonalWaystoneSettingsMenu, PersonalizedWaystoneImpl>() {
                @Override
                public PersonalWaystoneSettingsMenu create(int windowId, Inventory inventory, PersonalizedWaystoneImpl waystone) {
                    return new PersonalWaystoneSettingsMenu(windowId, waystone);
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, PersonalizedWaystoneImpl> getStreamCodec() {
                    return PersonalizedWaystoneImpl.STREAM_CODEC;
                }
            });

    public static void initialize() {
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }
}
