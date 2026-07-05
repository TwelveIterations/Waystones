package net.blay09.mods.waystones.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
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
        final var menuProvider = new WaystoneSelectionListBuilder(op)
                .withActivatedWaystones(target)
                .sorted(WaystoneComparators.forAdminInspection(op, target))
                .withFlags(Set.of(TeleportFlags.ADMIN))
                .skipSortingIndexUpdate()
                .buildMenuProvider(ModMenus.adminSelection.get(), Component.translatable("container.waystones.waystone_admin_selection", target.getScoreboardName()));
        Balm.getNetworking().openGui(op, menuProvider);

        return 0;
    }
}
