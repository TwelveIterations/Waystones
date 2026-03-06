package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public class ServerboundInventoryButtonPacket implements CustomPacketPayload {

    public static final ServerboundInventoryButtonPacket INSTANCE = new ServerboundInventoryButtonPacket();
    public static final CustomPacketPayload.Type<ServerboundInventoryButtonPacket> TYPE = new CustomPacketPayload.Type<>(id("inventory_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundInventoryButtonPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ServerboundInventoryButtonPacket() {
    }

    public static void handle(final ServerPlayer player, ServerboundInventoryButtonPacket message) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        if (player == null) {
            return;
        }

        // Reset cooldown if player is in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.resetCooldowns(player);
        }

        final var waystone = PlayerWaystoneManager.getInventoryButtonTarget(player);
        if (waystone.isPresent()) {
            WaystonesAPI.createDefaultTeleportContext(player, waystone.get(), it -> it.addFlag(TeleportFlags.INVENTORY_BUTTON))
                    .mapLeft(WaystonesAPI::tryTeleport);
        } else if (inventoryButtonMode.isReturnToAny()) {
            final var waystones = new ArrayList<>(PlayerWaystoneManager.getTargetsForInventoryButton(player));
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            final var containerProvider = new BalmMenuProvider<ModMenus.WaystoneListMenuData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                    return new WaystoneSelectionMenu(ModMenus.inventorySelection.value(), null, windowId, waystones, Collections.emptyMap(), Set.of(TeleportFlags.INVENTORY_BUTTON));
                }

                @Override
                public ModMenus.WaystoneListMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    final var warpRequirements = WaystoneSelectionMenu.buildWarpRequirements(serverPlayer, null, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
                    return new ModMenus.WaystoneListMenuData(waystones, warpRequirements);
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, ModMenus.WaystoneListMenuData> getScreenStreamCodec() {
                    return ModMenus.WaystoneListMenuData.STREAM_CODEC;
                }
            };
            Balm.networking().openMenu(player, containerProvider);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
