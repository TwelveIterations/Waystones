package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.menu.PlayerSelectionMenu;
import net.blay09.mods.waystones.requirement.PlayerTeleportRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

/**
 * 服务器端数据包 - 处理玩家传送到另一个玩家的请求
 */
public record ServerboundTeleportToPlayerPacket(UUID targetPlayerUuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundTeleportToPlayerPacket> TYPE = new CustomPacketPayload.Type<>(id("teleport_to_player"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTeleportToPlayerPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundTeleportToPlayerPacket::targetPlayerUuid,
            ServerboundTeleportToPlayerPacket::new
    );

    public static void handle(final ServerPlayer player, ServerboundTeleportToPlayerPacket message) {
        // 检查是否在正确的菜单中
        if (!(player.containerMenu instanceof PlayerSelectionMenu selectionMenu)) {
            return;
        }

        // 查找目标玩家
        var server = player.level().getServer();
        if (server != null) {
            ServerPlayer targetPlayer = server.getPlayerList().getPlayer(message.targetPlayerUuid());
            if (targetPlayer == null) {
                player.sendSystemMessage(Component.translatable("chat.waystones.player_not_found")
                        .withStyle(ChatFormatting.RED));
                return;
            }

            // 执行传送
            teleportPlayer(player, targetPlayer, selectionMenu);

            // 关闭菜单
            player.closeContainer();
        }
    }

    /**
     * 执行玩家到玩家的传送
     */
    private static void teleportPlayer(ServerPlayer sourcePlayer, ServerPlayer targetPlayer, PlayerSelectionMenu selectionMenu) {
        // 检查冷却时间
        Identifier cooldownKey = Identifier.fromNamespaceAndPath("waystones", "player_call");
        long remainingCooldownMillis = PlayerWaystoneManager.getCooldownMillisLeft(sourcePlayer, cooldownKey);
        if (remainingCooldownMillis > 0 && !sourcePlayer.getAbilities().instabuild) {
            sourcePlayer.sendSystemMessage(Component.translatable("chat.waystones.cooldown",
                            formatCooldownMillis(remainingCooldownMillis))
                    .withStyle(ChatFormatting.RED));
            return;
        }

        // 检查并消耗成本（经验等级）
        PlayerTeleportRequirement requirement = new PlayerTeleportRequirement(sourcePlayer, targetPlayer);
        if (!requirement.canAfford(sourcePlayer) && !sourcePlayer.getAbilities().instabuild) {
            sourcePlayer.sendSystemMessage(Component.translatable("chat.waystones.not_enough_xp")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        // 消耗成本
        requirement.consume(sourcePlayer);

        // 创建传送目标
        Vec3 targetLocation = new Vec3(
                targetPlayer.getX(),
                targetPlayer.getY(),
                targetPlayer.getZ()
        );
        ServerLevel targetLevel = targetPlayer.level();
        Direction direction = targetPlayer.getDirection();

        TeleportDestination destination = new TeleportDestination(targetLevel, targetLocation, direction);

        // 执行传送
        WaystoneTeleportManager.doTeleport(
                new net.blay09.mods.waystones.core.WaystoneTeleportContextImpl(sourcePlayer, null),
                destination
        );

        // 设置冷却时间
        int cooldownSeconds = WaystonesConfig.getActive().playerCall.cooldownSeconds;
        if (cooldownSeconds > 0 && !sourcePlayer.getAbilities().instabuild) {
            long cooldownUntil = System.currentTimeMillis() + cooldownSeconds * 1000L;
            PlayerWaystoneManager.setCooldownUntil(sourcePlayer, cooldownKey, cooldownUntil);
        }

        // 播放音效
        sourcePlayer.level().playSound(null, sourcePlayer.blockPosition(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.1f, 1f);
        targetPlayer.level().playSound(null, targetPlayer.blockPosition(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.1f, 1f);

        // 发送成功消息
        sourcePlayer.sendSystemMessage(Component.translatable("chat.waystones.teleported_to_player",
                        targetPlayer.getName())
                .withStyle(ChatFormatting.GREEN));
    }

    /**
     * 格式化冷却时间显示（毫秒）
     */
    private static String formatCooldownMillis(long millis) {
        int seconds = (int) (millis / 1000);
        if (seconds < 60) {
            return seconds + "s";
        }
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return minutes + "m " + remainingSeconds + "s";
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
