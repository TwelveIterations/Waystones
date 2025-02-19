package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.WaystoneInventoryButton;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class InventoryButtonGuiHandler {

    private static WaystoneInventoryButton warpButton;

    public static void initialize() {
        Balm.getEvents().onEvent(ScreenInitEvent.Post.class, event -> {
            Screen screen = event.getScreen();
            if (!(screen instanceof InventoryScreen) && !(screen instanceof CreativeModeInventoryScreen)) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (screen != mc.screen) {
                return;
            }

            InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
            if (!inventoryButtonMode.isEnabled()) {
                return;
            }

            Supplier<Integer> xPosition = screen instanceof CreativeModeInventoryScreen ? () -> WaystonesConfig.getActive().inventoryButton.creativeWarpButtonX : () -> WaystonesConfig.getActive().inventoryButton.warpButtonX;
            Supplier<Integer> yPosition = screen instanceof CreativeModeInventoryScreen ? () -> WaystonesConfig.getActive().inventoryButton.creativeWarpButtonY : () -> WaystonesConfig.getActive().inventoryButton.warpButtonY;
            warpButton = new WaystoneInventoryButton((AbstractContainerScreen<?>) screen, button -> {
                Player player = mc.player;

                // Reset cooldown if player is in creative mode
                if (player.getAbilities().instabuild) {
                    PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
                }

                if (PlayerWaystoneManager.canUseInventoryButton(player)) {
                    if (inventoryButtonMode.hasNamedTarget()) {
                        mc.setScreen(new InventoryButtonReturnConfirmScreen(inventoryButtonMode.getNamedTarget()));
                    } else if (inventoryButtonMode.isReturnToNearest()) {
                        if (PlayerWaystoneManager.getNearestWaystone(player) != null) {
                            mc.setScreen(new InventoryButtonReturnConfirmScreen());
                        }
                    } else if (inventoryButtonMode.isReturnToAny()) {
                        Balm.getNetworking().sendToServer(new InventoryButtonMessage());
                    }
                } else {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.5f));
                }
            }, () -> {
                if (screen instanceof CreativeModeInventoryScreen creativeModeInventoryScreen) {
                    return creativeModeInventoryScreen.isInventoryOpen();
                }

                return true;
            }, xPosition, yPosition);
            BalmClient.getScreens().addRenderableWidget(screen, warpButton);
        });

        Balm.getEvents().onEvent(ScreenDrawEvent.Post.class, event -> {
            Screen screen = event.getScreen();
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            // Render the inventory button tooltip when it's hovered
            if ((screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) && warpButton != null && warpButton.isHoveredOrFocused()) {
                InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
                List<Component> tooltip = new ArrayList<>();
                Player player = Minecraft.getInstance().player;
                if (player == null) {
                    return;
                }

                long timeLeft = PlayerWaystoneManager.getInventoryButtonCooldownLeft(player);
                IWaystone waystone = PlayerWaystoneManager.getInventoryButtonWaystone(player);
                int xpLevelCost = waystone != null ? PlayerWaystoneManager.predictExperienceLevelCost(player, waystone, WarpMode.INVENTORY_BUTTON, (IWaystone) null) : 0;
                int secondsLeft = (int) (timeLeft / 1000);
                if (inventoryButtonMode.hasNamedTarget()) {
                    tooltip.add(formatTranslation(ChatFormatting.YELLOW, "gui.waystones.inventory.return_to_waystone"));
                    tooltip.add(formatTranslation(ChatFormatting.GRAY, "tooltip.waystones.bound_to", ChatFormatting.DARK_AQUA + inventoryButtonMode.getNamedTarget()));
                    if (secondsLeft > 0) {
                        tooltip.add(Component.empty());
                    }
                } else if (inventoryButtonMode.isReturnToNearest()) {
                    tooltip.add(formatTranslation(ChatFormatting.YELLOW, "gui.waystones.inventory.return_to_nearest_waystone"));
                    IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
                    if (nearestWaystone != null) {
                        tooltip.add(formatTranslation(ChatFormatting.GRAY, "tooltip.waystones.bound_to", ChatFormatting.DARK_AQUA + nearestWaystone.getName()));
                    } else {
                        tooltip.add(formatTranslation(ChatFormatting.RED, "gui.waystones.inventory.no_waystones_activated"));
                    }
                    if (secondsLeft > 0) {
                        tooltip.add(Component.empty());
                    }
                } else if (inventoryButtonMode.isReturnToAny()) {
                    tooltip.add(formatTranslation(ChatFormatting.YELLOW, "gui.waystones.inventory.return_to_waystone"));
                    if (PlayerWaystoneManager.getWaystones(player).isEmpty()) {
                        tooltip.add(formatTranslation(ChatFormatting.RED, "gui.waystones.inventory.no_waystones_activated"));
                    }
                }

                if (xpLevelCost > 0 && player.experienceLevel < xpLevelCost) {
                    tooltip.add(formatTranslation(ChatFormatting.RED, "tooltip.waystones.not_enough_xp", xpLevelCost));
                }

                if (secondsLeft > 0) {
                    tooltip.add(formatTranslation(ChatFormatting.GOLD, "tooltip.waystones.cooldown_left", secondsLeft));
                }

                guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        });
    }

    private static Component formatTranslation(ChatFormatting formatting, String key, Object... args) {
        final var result = Component.translatable(key, args);
        result.withStyle(formatting);
        return result;
    }

}
