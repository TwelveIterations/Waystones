package net.blay09.mods.waystones.client;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.gui.screens.BalmScreenUtils;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.event.callback.ScreenCallback;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.WaystoneInventoryButton;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.ServerboundInventoryButtonPacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestInventoryButtonPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InventoryButtonGuiHandler {

    private static @Nullable WaystoneInventoryButton warpButton;
    private static int refreshTicks;
    private static Either<List<Object>, List<Object>> requirements = Either.right(List.of());

    public static void updateRequirements(Either<List<Object>, List<Object>> requirements) {
        InventoryButtonGuiHandler.requirements = requirements;
    }

    public static Either<List<Object>, List<Object>> getRequirements() {
        return requirements;
    }

    public static void initialize() {
        ClientLifecycleCallback.DisconnectedFromServer.EVENT.register(_ -> {
            requirements = Either.right(List.of());
            warpButton = null;
            refreshTicks = 0;
        });
        ClientTickCallback.AFTER.register(client -> {
            final var screen = client.gui.screen();
            if ((screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) && client.player != null
                    && WaystonesConfig.getActive().getInventoryButtonMode().isEnabled() && --refreshTicks <= 0) {
                Balm.networking().sendToServer(ServerboundRequestInventoryButtonPacket.INSTANCE);
                refreshTicks = 20;
            }
        });
        ScreenCallback.Init.After.EVENT.register(screen -> {
            warpButton = null;
            if (!(screen instanceof InventoryScreen) && !(screen instanceof CreativeModeInventoryScreen)) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (screen != mc.gui.screen()) {
                return;
            }

            InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
            if (!inventoryButtonMode.isEnabled()) {
                return;
            }

            Supplier<Integer> xPosition = screen instanceof CreativeModeInventoryScreen ? () -> WaystonesConfig.getActive().inventoryButton.creativeInventoryButtonX : () -> WaystonesConfig.getActive().inventoryButton.inventoryButtonX;
            Supplier<Integer> yPosition = screen instanceof CreativeModeInventoryScreen ? () -> WaystonesConfig.getActive().inventoryButton.creativeInventoryButtonY : () -> WaystonesConfig.getActive().inventoryButton.inventoryButtonY;
            warpButton = new WaystoneInventoryButton((AbstractContainerScreen<?>) screen, _ -> {
                Player player = mc.player;
                if (player == null) {
                    return;
                }

                if (requirements.left().isPresent()) {
                    if (inventoryButtonMode.hasNamedTarget()) {
                        mc.gui.setScreen(new InventoryButtonReturnConfirmScreen(inventoryButtonMode.getNamedTarget()));
                    } else if (inventoryButtonMode.isReturnToNearest()) {
                        if (PlayerWaystoneManager.getNearestWaystone(player).isPresent()) {
                            mc.gui.setScreen(new InventoryButtonReturnConfirmScreen());
                        }
                    } else if (inventoryButtonMode.isReturnToAny()) {
                        Balm.networking().sendToServer(ServerboundInventoryButtonPacket.INSTANCE);
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
            BalmScreenUtils.addRenderableWidget(screen, warpButton);
            refreshTicks = 20;
            Balm.networking().sendToServer(ServerboundRequestInventoryButtonPacket.INSTANCE);
        });

        ScreenCallback.Render.AFTER.register((screen, graphics) -> {
            // Render the inventory button tooltip when it's hovered
            if ((screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) && warpButton != null && warpButton.isHighlighted()) {
                InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
                List<Component> tooltip = new ArrayList<>();
                Player player = Minecraft.getInstance().player;
                if (player == null) {
                    return;
                }

                if (inventoryButtonMode.hasNamedTarget()) {
                    tooltip.add(Component.translatable("gui.waystones.inventory.return_to_waystone").withStyle(ChatFormatting.YELLOW));
                    final var targetComponent = Component.literal(inventoryButtonMode.getNamedTarget()).withStyle(ChatFormatting.DARK_AQUA);
                    tooltip.add(Component.translatable("tooltip.waystones.bound_to", targetComponent).withStyle(ChatFormatting.GRAY));
                } else if (inventoryButtonMode.isReturnToNearest()) {
                    tooltip.add(Component.translatable("gui.waystones.inventory.return_to_nearest_waystone").withStyle(ChatFormatting.YELLOW));
                    final var nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
                    tooltip.add(nearestWaystone.map(it -> it.getEffectiveName().copy().withStyle(ChatFormatting.DARK_AQUA))
                            .map(it -> Component.translatable("tooltip.waystones.bound_to", it).withStyle(ChatFormatting.GRAY))
                            .orElseGet(() -> Component.translatable("gui.waystones.inventory.no_waystones_activated").withStyle(ChatFormatting.RED)));
                } else if (inventoryButtonMode.isReturnToAny()) {
                    tooltip.add(Component.translatable("gui.waystones.inventory.return_to_waystone").withStyle(ChatFormatting.YELLOW));
                    if (PlayerWaystoneManager.getActivatedWaystones(player).isEmpty()) {
                        tooltip.add(Component.translatable("gui.waystones.inventory.no_waystones_activated").withStyle(ChatFormatting.RED));
                    }
                }

                requirements.ifRight(failures -> RequirementClientRegistry.getErrorListRenderer().appendHoverText(player, failures, tooltip));

                final var font = Minecraft.getInstance().font;
                final var visualTooltip = tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
                graphics.tooltip(font, visualTooltip, graphics.mouseX(), graphics.mouseY(), DefaultTooltipPositioner.INSTANCE, null, false);
            }
        });
    }

}
