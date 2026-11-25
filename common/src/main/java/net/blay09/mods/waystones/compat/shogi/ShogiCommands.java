package net.blay09.mods.waystones.compat.shogi;

import net.blay09.mods.balm.commands.BalmCommands;
import net.blay09.mods.shogi.common.effect.server.cooldown.ShogiCooldowns;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import java.util.HashSet;
import static net.minecraft.commands.Commands.argument;

public class ShogiCommands {
    private static final Identifier PERMISSION_WAYSTONES_COOLDOWN = Waystones.id("command.waystones.cooldown");

    public static void initialize(BalmCommands commands) {
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_COOLDOWN, Permissions.COMMANDS_GAMEMASTER);
        commands.register(dispatcher -> dispatcher.register(Commands.literal("waystones")
                .then(Commands.literal("cooldown")
                        // TODO .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_COOLDOWN))
                        .requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS))
                        .then(argument("targets", EntityArgument.players())
                                .then(Commands.literal("reset")
                                        .then(Commands.literal("all").executes(context -> {
                                            final var targets = EntityArgument.getPlayers(context, "targets");
                                            for (final var player : targets) {
                                                ShogiCooldowns.get(player).resetAllCooldowns();
                                            }

                                            if (targets.size() == 1) {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.all.success.single",
                                                        targets.iterator().next().getDisplayName()), true);
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.all.success.multiple",
                                                        targets.size()), true);
                                            }
                                            return targets.size();
                                        }))
                                        .then(argument("identifier", StringArgument.word())
                                                .suggests(SuggestionProviders.serverOnly((server, context, builder) -> {
                                                    try {
                                                        final var targets = EntityArgument.getSuggestedEntities(server, context, "targets")
                                                                .filter(ServerPlayer.class::isInstance)
                                                                .map(ServerPlayer.class::cast)
                                                                .toList();
                                                        final var keys = new HashSet<String>();
                                                        for (final var player : targets) {
                                                            for (final var key : ShogiCooldowns.get(player).getCooldownIds()) {
                                                                keys.add(key.toString());
                                                            }
                                                        }
                                                        return SuggestionContext.suggest(keys, builder);
                                                    } catch (Exception e) {
                                                        return builder.buildFuture();
                                                    }
                                                }))
                                                .executes(context -> {
                                                    final var targets = EntityArgument.getPlayers(context, "targets");
                                                    final var identifierStr = StringArgument.getString(context, "identifier");
                                                    final var cooldownKey = Identifier.tryParse(identifierStr);
                                                    if (cooldownKey == null) {
                                                        context.getSource().sendFailure(Component.translatable("commands.waystones.cooldown.reset.invalid_identifier", identifierStr));
                                                        return 0;
                                                    }

                                                    for (final var player : targets) {
                                                        ShogiCooldowns.get(player).resetCooldown(cooldownKey);
                                                    }

                                                    if (targets.size() == 1) {
                                                        context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.single",
                                                                cooldownKey.toString(), targets.iterator().next().getDisplayName()), true);
                                                    } else {
                                                        context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.multiple",
                                                                cooldownKey.toString(), targets.size()), true);
                                                    }
                                                    return targets.size();
                                                })))))
        ));
    }
}
