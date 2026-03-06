package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.PlayerInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 玩家选择菜单 - 允许玩家选择要传送到的目标玩家
 */
public class PlayerSelectionMenu extends AbstractContainerMenu {

    private final List<PlayerInfo> playerInfos;
    private Consumer<PlayerTeleportContext> postTeleportHandler = it -> {};
    private ItemStack warpItem = ItemStack.EMPTY;
    private InteractionHand warpHand = InteractionHand.MAIN_HAND;

    public PlayerSelectionMenu(MenuType<?> type, int windowId, List<PlayerInfo> playerInfos) {
        super(type, windowId);
        this.playerInfos = playerInfos != null ? playerInfos : List.of();
    }

    /**
     * 从 ServerPlayer 列表创建 PlayerSelectionMenu 的静态工厂方法
     */
    public static PlayerSelectionMenu fromServerPlayers(MenuType<?> type, int windowId, List<ServerPlayer> serverPlayers) {
        List<PlayerInfo> playerInfos = serverPlayers.stream()
                .map(p -> new PlayerInfo(p.getUUID(), p.getName().getString(), p.level().dimension(), p.blockPosition()))
                .collect(Collectors.toList());
        return new PlayerSelectionMenu(type, windowId, playerInfos);
    }

    public PlayerSelectionMenu withWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    public PlayerSelectionMenu withHand(InteractionHand hand) {
        this.warpHand = hand;
        return this;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public List<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    /**
     * @deprecated 使用 getPlayerInfos 代替
     */
    @Deprecated
    public List<ServerPlayer> getPlayers() {
        // 此方法保留用于兼容性，但不应再使用
        return List.of();
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }

    public InteractionHand getWarpHand() {
        return warpHand;
    }

    public Consumer<PlayerTeleportContext> getPostTeleportHandler() {
        return postTeleportHandler;
    }

    public PlayerSelectionMenu setPostTeleportHandler(Consumer<PlayerTeleportContext> postTeleportHandler) {
        this.postTeleportHandler = postTeleportHandler;
        return this;
    }

    /**
     * 玩家传送上下文 - 封装传送所需的信息
     */
    public static class PlayerTeleportContext {
        private final ServerPlayer sourcePlayer;
        private final ServerPlayer targetPlayer;
        private final ItemStack warpItem;
        private final InteractionHand warpHand;

        public PlayerTeleportContext(ServerPlayer sourcePlayer, ServerPlayer targetPlayer, ItemStack warpItem, InteractionHand warpHand) {
            this.sourcePlayer = sourcePlayer;
            this.targetPlayer = targetPlayer;
            this.warpItem = warpItem;
            this.warpHand = warpHand;
        }

        public ServerPlayer getSourcePlayer() {
            return sourcePlayer;
        }

        public ServerPlayer getTargetPlayer() {
            return targetPlayer;
        }

        public ItemStack getWarpItem() {
            return warpItem;
        }

        public InteractionHand getWarpHand() {
            return warpHand;
        }
    }
}
