package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.InternalClientMethodsImpl;
import net.blay09.mods.waystones.api.client.WaystonesClientAPI;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.compat.Compat;
import net.minecraft.client.ClientBrandRetriever;

import java.util.Locale;

public class WaystonesClient {
    public static void initialize() {
        WaystonesClientAPI.__internalMethods = new InternalClientMethodsImpl();
        RequirementClientRegistry.registerDefaults();

        ModClientEventHandlers.initialize();
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModModels.initialize(BalmClient.getModels());
        ModItemProperties.initialize();

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);

        /* TODO Balm.getEvents().onEvent(UseItemInputEvent.class, event -> {
            final var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            if (mc.player.isShiftKeyDown()) {
                final var blockHitResult = (BlockHitResult) mc.hitResult;
                final var targetBlockEntity = mc.level.getBlockEntity(blockHitResult.getBlockPos());
                if (targetBlockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                    Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(waystoneBlockEntity.getBlockPos()));
                    mc.player.swing(event.getHand());
                    event.setCanceled(true);
                }
            }
        });*/
    }
}
