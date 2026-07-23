package net.blay09.mods.waystones.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CountWaystonesCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
        final var waystones = PlayerWaystoneManager.getActivatedWaystones(player);
        int total = waystones.size();
        long owned = waystones.stream().filter(w -> w.isOwner(player)).count();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.waystones.count", player.getScoreboardName(), total, owned), false);
        return waystones.size();
    }
}
