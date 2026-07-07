package net.blay09.mods.waystones.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class OpenPlayerWaystonesGuiCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
        ServerPlayer op = ctx.getSource().getPlayerOrException();
        final var waystones = PlayerWaystoneManager.getPlayerDecoratedWaystones(target, PlayerWaystoneManager.getActivatedWaystones(target));
        final var menuProvider = new WaystoneSelectionListBuilder(op)
                .withDecoratedWaystones(waystones)
                .withFlags(Set.of(TeleportFlags.ADMIN))
                .sorted(WaystoneComparators.forAdminInspection(op, target))
                .skipSortingIndexUpdate()
                .buildMenuProvider(ModMenus.adminSelection.value(), Component.translatable("container.waystones.waystone_admin_selection", target.getScoreboardName()));
        Balm.networking().openMenu(op, menuProvider);

        return 0;
    }
}
