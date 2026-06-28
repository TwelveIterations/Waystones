package net.blay09.mods.waystones.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneStyle;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.SharedSuggestionProvider;

import static net.minecraft.commands.Commands.argument;

public class ModCommands {
    private static final SimpleCommandExceptionType ERROR_WAYSTONE_NOT_FOUND = new SimpleCommandExceptionType(Component.translatable(
            "commands.waystones.waystone_not_found"));

    private static final ResourceLocation PERMISSION_WAYSTONES_ACTIVATE = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.activate");
    private static final ResourceLocation PERMISSION_WAYSTONES_FORGET = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.forget");
    private static final ResourceLocation PERMISSION_WAYSTONES_COUNT = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.count");
    private static final ResourceLocation PERMISSION_WAYSTONES_LIST = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.list");
    private static final ResourceLocation PERMISSION_WAYSTONES_PLACE = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.place");
    private static final ResourceLocation PERMISSION_WAYSTONES_GUI = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.gui");
    private static final ResourceLocation PERMISSION_WAYSTONES_COOLDOWN = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "command.waystones.cooldown");

    public static void initialize(BalmCommands commands) {
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_ACTIVATE, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_FORGET, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_COUNT, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_PLACE, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_LIST, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_GUI, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_COOLDOWN, 2);
        commands.register(dispatcher -> dispatcher.register(Commands.literal("waystones")
                .requires(BalmCommands.requireAnyPermission(PERMISSION_WAYSTONES_ACTIVATE, PERMISSION_WAYSTONES_FORGET, PERMISSION_WAYSTONES_COUNT, PERMISSION_WAYSTONES_LIST, PERMISSION_WAYSTONES_GUI, PERMISSION_WAYSTONES_PLACE, PERMISSION_WAYSTONES_COOLDOWN))
                .then(Commands.literal("activate")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_ACTIVATE))
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("pos", BlockPosArgument.blockPos()).executes(context -> {
                                    final var targets = EntityArgument.getPlayers(context, "targets");
                                    final var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                    final var foundWaystone = WaystonesAPI.getWaystoneAt(context.getSource().getLevel(), pos);
                                    if (foundWaystone.isPresent()) {
                                        final var waystone = foundWaystone.get();
                                        for (final var player : targets) {
                                            WaystonesAPI.activateWaystone(player, waystone);
                                        }

                                        if (targets.size() == 1) {
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.activate.success.single",
                                                    waystone.getEffectiveName(), targets.iterator().next().getDisplayName()), true);
                                        } else {
                                            context.getSource()
                                                    .sendSuccess(() -> Component.translatable("commands.waystones.activate.success.multiple",
                                                            waystone.getEffectiveName(),
                                                            targets.size()), true);
                                        }
                                    } else {
                                        throw ERROR_WAYSTONE_NOT_FOUND.create();
                                    }
                                    return targets.size();
                                }))))
                .then(Commands.literal("forget")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_FORGET))
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("pos", BlockPosArgument.blockPos()).executes(context -> {
                                    final var targets = EntityArgument.getPlayers(context, "targets");
                                    final var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                    final var foundWaystone = WaystonesAPI.getWaystoneAt(context.getSource().getLevel(), pos);
                                    if (foundWaystone.isPresent()) {
                                        final var waystone = foundWaystone.get();
                                        for (final var player : targets) {
                                            WaystonesAPI.deactivateWaystone(player, waystone);
                                        }

                                        if (targets.size() == 1) {
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.forget.success.single",
                                                    waystone.getEffectiveName(), targets.iterator().next().getDisplayName()), true);
                                        } else {
                                            context.getSource()
                                                    .sendSuccess(() -> Component.translatable("commands.waystones.forget.success.multiple",
                                                            waystone.getEffectiveName(),
                                                            targets.size()), true);
                                        }
                                    } else {
                                        throw ERROR_WAYSTONE_NOT_FOUND.create();
                                    }
                                    return targets.size();
                                }))
                                .then(Commands.literal("all").executes(context -> {
                                    final var targets = EntityArgument.getPlayers(context, "targets");
                                    int totalDeactivated = 0;
                                    for (final var player : targets) {
                                        final var waystones = PlayerWaystoneManager.getActivatedWaystones(player);
                                        for (final var waystone : waystones) {
                                            WaystonesAPI.deactivateWaystone(player, waystone);
                                        }
                                        totalDeactivated += waystones.size();
                                    }

                                    if (targets.size() == 1) {
                                        context.getSource()
                                                .sendSuccess(() -> Component.translatable("commands.waystones.forget.all.success.single",
                                                        targets.iterator().next().getDisplayName()), true);
                                    } else {
                                        context.getSource()
                                                .sendSuccess(() -> Component.translatable("commands.waystones.forget.all.success.multiple",
                                                        targets.size()), true);
                                    }
                                    return totalDeactivated;
                                }))))

                .then(Commands.literal("place")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_PLACE))
                        .then(argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("style", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            return SharedSuggestionProvider.suggest(
                                                    WaystoneStyles.getRegisteredKeys().stream()
                                                            .map(id -> id.getNamespace().equals(Waystones.MOD_ID) ? id.getPath() : id.toString()),
                                                    builder
                                            );
                                        })
                                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    final var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                                    final var styleKey = StringArgumentType.getString(context, "style");
                                                    final var name = StringArgumentType.getString(context, "name");
                                                    final ServerLevel level = context.getSource().getLevel();

                                                    ResourceLocation styleId;
                                                    if (!styleKey.contains(":")) {
                                                        styleId = ResourceLocation.fromNamespaceAndPath("waystones", styleKey);
                                                    } else {
                                                        styleId = ResourceLocation.tryParse(styleKey);
                                                    }

                                                    WaystoneStyle style = styleId != null ? WaystoneStyles.getStyle(styleId) : null;
                                                    if (style == null) {
                                                        context.getSource().sendFailure(Component.literal("Unknown waystone style: " + styleKey));
                                                        return 0;
                                                    }

                                                    WaystonesAPI.placeWaystone(level, pos, style).ifPresent(waystone -> {
                                                        ((MutableWaystone) waystone).setName(Component.literal(name));
                                                    });
                                                    return 1;
                                                })))))
                .then(Commands.literal("count")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_COUNT))
                        .then(argument("player", EntityArgument.player()).executes(new CountWaystonesCommand())))
                .then(Commands.literal("list")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_LIST))
                        .then(argument("player", EntityArgument.player()).executes(ctx -> {
                                    final var caller = ctx.getSource().getPlayerOrException();
                                    final var target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
                                    final var waystones = PlayerWaystoneManager.getActivatedWaystones(target)
                                            .stream()
                                            .filter(it -> it.isOwner(target))
                                            .sorted(WaystoneComparators.forAdminInspection(caller, target))
                                            .toList();
                                    ctx.getSource().sendSystemMessage(Component.translatable("commands.waystones.list.header", target.getScoreboardName()));
                                    for (var waystone : waystones) {
                                        ctx.getSource().sendSystemMessage(componentForWaystoneList(caller, target, waystone));
                                    }
                                    final var result = Component.translatable("commands.waystones.list.footer", waystones.size());
                                    ctx.getSource().sendSuccess(() -> result, false);
                                    return waystones.size();
                                })
                                .then(Commands.literal("all").executes(ctx -> {
                                    final var caller = ctx.getSource().getPlayerOrException();
                                    final var target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
                                    final var waystones = PlayerWaystoneManager.getActivatedWaystones(target)
                                            .stream()
                                            .sorted(WaystoneComparators.forAdminInspection(caller, target))
                                            .toList();
                                    ctx.getSource().sendSystemMessage(Component.translatable("commands.waystones.list.all.header", target.getScoreboardName()));
                                    for (var waystone : waystones) {
                                        ctx.getSource().sendSystemMessage(componentForWaystoneList(caller, target, waystone));
                                    }
                                    final var ownedCount = waystones.stream().filter(it -> it.isOwner(target)).count();
                                    final var result = Component.translatable("commands.waystones.list.all.footer", waystones.size(), ownedCount);
                                    ctx.getSource().sendSuccess(() -> result, false);
                                    return waystones.size();
                                }))))
                .then(Commands.literal("gui")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_GUI))
                        .then(argument("player", EntityArgument.player()).executes(new OpenPlayerWaystonesGuiCommand())))
                .then(Commands.literal("cooldown")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_COOLDOWN))
                        .then(argument("targets", EntityArgument.players())
                                .then(Commands.literal("reset")
                                        .then(Commands.literal("all").executes(context -> {
                                            final var targets = EntityArgument.getPlayers(context, "targets");
                                            for (final var player : targets) {
                                                PlayerWaystoneManager.resetCooldowns(player);
                                                WaystoneSyncManager.sendWaystoneCooldowns(player);
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
                                        .then(argument("identifier", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    try {
                                                        final var targets = EntityArgument.getPlayers(context, "targets");
                                                        final var keys = new java.util.HashSet<String>();
                                                        for (final var player : targets) {
                                                            for (final var key : PlayerWaystoneManager.getCooldowns(player).keySet()) {
                                                                keys.add(key.toString());
                                                            }
                                                        }
                                                        return SharedSuggestionProvider.suggest(keys, builder);
                                                    } catch (Exception e) {
                                                        return builder.buildFuture();
                                                    }
                                                })
                                                .executes(context -> {
                                                    final var targets = EntityArgument.getPlayers(context, "targets");
                                                    final var identifierStr = StringArgumentType.getString(context, "identifier");
                                                    final var cooldownKey = ResourceLocation.tryParse(identifierStr);
                                                    if (cooldownKey == null) {
                                                        context.getSource().sendFailure(Component.translatable("commands.waystones.cooldown.reset.invalid_identifier", identifierStr));
                                                        return 0;
                                                    }

                                                    for (final var player : targets) {
                                                        PlayerWaystoneManager.setCooldownUntil(player, cooldownKey, 0);
                                                        WaystoneSyncManager.sendWaystoneCooldowns(player);
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

    private static Component componentForWaystoneList(ServerPlayer caller, ServerPlayer target, Waystone waystone) {
        final var waystoneDimensionId = waystone.getDimension().location();
        final var waystonePos = waystone.getPos();
        Component location;
        if (waystone.getDimension() != caller.level().dimension()) {
            location = Component.translatable("commands.waystones.list.in_dimension", waystoneDimensionId.toString());
        } else {
            final var distance = (int) Math.sqrt(caller.distanceToSqr(waystone.getPos().getCenter()));
            location = Component.translatable("commands.waystones.list.at_distance", distance);
        }

        final var suggestedCommand = String.format("/execute in %s run teleport %d %d %d",
                waystoneDimensionId,
                waystonePos.getX(),
                waystonePos.getY(),
                waystonePos.getZ());

        final var coordinates = Component.translatable("commands.waystones.list.coordinates", waystonePos.getX(), waystonePos.getY(), waystonePos.getZ())
                .withStyle(ChatFormatting.YELLOW)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var waystoneName = PlayerWaystoneManager.getPlayerDecoratedWaystone(target, waystone).getEffectiveName().copy()
                .withStyle(ChatFormatting.GREEN)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var entryKey = waystone.isOwner(target) ? "commands.waystones.list.entry.owned" : "commands.waystones.list.entry.activated";
        return Component.translatable(entryKey, location, coordinates, waystoneName);
    }
}
