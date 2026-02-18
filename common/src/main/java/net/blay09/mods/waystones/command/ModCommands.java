package net.blay09.mods.waystones.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneStyle;
import net.blay09.mods.waystones.api.WaystoneStyles;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;

public class ModCommands {
    private static final SimpleCommandExceptionType ERROR_WAYSTONE_NOT_FOUND = new SimpleCommandExceptionType(Component.translatable(
            "commands.waystones.waystone_not_found"));

    private static final ResourceLocation PERMISSION_WAYSTONES_ACTIVATE = new ResourceLocation(Waystones.MOD_ID, "command.waystones.activate");
    private static final ResourceLocation PERMISSION_WAYSTONES_FORGET = new ResourceLocation(Waystones.MOD_ID, "command.waystones.forget");
    private static final ResourceLocation PERMISSION_WAYSTONES_COUNT = new ResourceLocation(Waystones.MOD_ID, "command.waystones.count");
    private static final ResourceLocation PERMISSION_WAYSTONES_LIST = new ResourceLocation(Waystones.MOD_ID, "command.waystones.list");
    private static final ResourceLocation PERMISSION_WAYSTONES_PLACE = new ResourceLocation(Waystones.MOD_ID, "command.waystones.place");
    private static final ResourceLocation PERMISSION_WAYSTONES_COOLDOWN = new ResourceLocation(Waystones.MOD_ID, "command.waystones.cooldown");

    public static void initialize(BalmCommands commands) {
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_ACTIVATE, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_FORGET, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_COUNT, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_PLACE, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_LIST, 2);
        BalmCommands.registerPermission(PERMISSION_WAYSTONES_COOLDOWN, 2);
        commands.register(dispatcher -> dispatcher.register(Commands.literal("waystones")
                .requires(BalmCommands.requireAnyPermission(PERMISSION_WAYSTONES_ACTIVATE, PERMISSION_WAYSTONES_FORGET, PERMISSION_WAYSTONES_COUNT, PERMISSION_WAYSTONES_LIST, PERMISSION_WAYSTONES_PLACE, PERMISSION_WAYSTONES_COOLDOWN))
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
                                            PlayerWaystoneManager.activateWaystone(player, waystone);
                                            WaystoneSyncManager.sendActivatedWaystones(player);
                                        }

                                        if (targets.size() == 1) {
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.activate.success.single",
                                                    waystone.getName(), targets.iterator().next().getDisplayName()), true);
                                        } else {
                                            context.getSource()
                                                    .sendSuccess(() -> Component.translatable("commands.waystones.activate.success.multiple",
                                                            waystone.getName(),
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
                                            PlayerWaystoneManager.deactivateWaystone(player, waystone);
                                            WaystoneSyncManager.sendActivatedWaystones(player);
                                        }

                                        if (targets.size() == 1) {
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.forget.success.single",
                                                    waystone.getName(), targets.iterator().next().getDisplayName()), true);
                                        } else {
                                            context.getSource()
                                                    .sendSuccess(() -> Component.translatable("commands.waystones.forget.success.multiple",
                                                            waystone.getName(),
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
                                        final var waystones = PlayerWaystoneManager.getWaystones(player);
                                        for (final var waystone : waystones) {
                                            PlayerWaystoneManager.deactivateWaystone(player, waystone);
                                            WaystoneSyncManager.sendActivatedWaystones(player);
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
                                                        styleId = new ResourceLocation("waystones", styleKey);
                                                    } else {
                                                        styleId = ResourceLocation.tryParse(styleKey);
                                                    }

                                                    WaystoneStyle style = styleId != null ? WaystoneStyles.getStyle(styleId) : null;
                                                    if (style == null) {
                                                        context.getSource().sendFailure(Component.literal("Unknown waystone style: " + styleKey));
                                                        return 0;
                                                    }

                                                    WaystonesAPI.placeWaystone(level, pos, style).ifPresent(waystone -> {
                                                        ((Waystone) waystone).setName(name);
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
                                    final var waystones = PlayerWaystoneManager.getWaystones(target)
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
                                    final var waystones = PlayerWaystoneManager.getWaystones(target)
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
                .then(Commands.literal("cooldown")
                        .requires(BalmCommands.requirePermission(PERMISSION_WAYSTONES_COOLDOWN))
                        .then(argument("targets", EntityArgument.players())
                                .then(Commands.literal("reset")
                                        .then(Commands.literal("all").executes(context -> {
                                            final var targets = EntityArgument.getPlayers(context, "targets");
                                            for (final var player : targets) {
                                                PlayerWaystoneManager.setWarpStoneCooldownUntil(player, 0);
                                                PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
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
                                        .then(Commands.literal("warp_stone").executes(context -> {
                                            final var targets = EntityArgument.getPlayers(context, "targets");

                                            for (final var player : targets) {
                                                PlayerWaystoneManager.setWarpStoneCooldownUntil(player, 0);
                                                WaystoneSyncManager.sendWaystoneCooldowns(player);
                                            }

                                            if (targets.size() == 1) {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.single",
                                                        "warp_stone", targets.iterator().next().getDisplayName()), true);
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.multiple",
                                                        "warp_stone", targets.size()), true);
                                            }
                                            return targets.size();
                                        }))
                                        .then(Commands.literal("inventory_button").executes(context -> {
                                            final var targets = EntityArgument.getPlayers(context, "targets");

                                            for (final var player : targets) {
                                                PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
                                                WaystoneSyncManager.sendWaystoneCooldowns(player);
                                            }

                                            if (targets.size() == 1) {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.single",
                                                        "inventory_button", targets.iterator().next().getDisplayName()), true);
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.waystones.cooldown.reset.success.multiple",
                                                        "inventory_button", targets.size()), true);
                                            }
                                            return targets.size();
                                        })))))
        ));
    }

    private static Component componentForWaystoneList(ServerPlayer caller, ServerPlayer target, IWaystone waystone) {
        final var waystoneDimensionId = waystone.getDimension().location();
        final var waystonePos = waystone.getPos();
        Component location;
        if (waystone.getDimension() != caller.level().dimension()) {
            location = Component.translatable("commands.waystones.list.in_dimension", waystoneDimensionId.toString());
        } else {
            final var distance = (int) caller.position().distanceTo(waystonePos.getCenter());
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

        final var waystoneName = Component.literal(waystone.getName())
                .withStyle(ChatFormatting.GREEN)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var entryKey = waystone.isOwner(target) ? "commands.waystones.list.entry.owned" : "commands.waystones.list.entry.activated";
        return Component.translatable(entryKey, location, coordinates, waystoneName);
    }
}
