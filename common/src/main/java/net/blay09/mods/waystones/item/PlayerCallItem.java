package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.PlayerInfo;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.PlayerSelectionMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 玩家传送物品 - 允许玩家传送到其他在线玩家
 */
public class PlayerCallItem extends Item {

    public PlayerCallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        final var itemStack = player.getItemInHand(hand);

        if (!world.isClientSide()) {
            // 播放音效
            world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);

            // 获取服务器在线玩家列表
            List<ServerPlayer> onlinePlayers = getPlayerList((ServerPlayer) player);

            // 打开玩家选择菜单
            Balm.networking().openMenu(player, new BalmMenuProvider<ModMenus.PlayerSelectionMenuData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.player_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                    return PlayerSelectionMenu.fromServerPlayers(ModMenus.playerSelection.value(), windowId, onlinePlayers)
                            .withWarpItem(itemStack)
                            .withHand(hand);
                }

                @Override
                public ModMenus.PlayerSelectionMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    List<PlayerInfo> playerInfos = onlinePlayers.stream()
                            .map(p -> new PlayerInfo(p.getUUID(), p.getName().getString(), p.level().dimension(), p.blockPosition()))
                            .collect(Collectors.toList());

                    // 【测试用】如果没有其他玩家，添加一些模拟玩家数据
                    if (playerInfos.isEmpty()) {
                        playerInfos = getMockPlayerInfos(serverPlayer);
                    }

                    return new ModMenus.PlayerSelectionMenuData(playerInfos, itemStack);
                }

                /**
                 * 获取模拟玩家信息列表（仅用于测试UI显示）
                 */
                private List<PlayerInfo> getMockPlayerInfos(ServerPlayer currentPlayer) {
                    List<PlayerInfo> mockPlayers = new ArrayList<>();
                    ResourceKey<Level> currentDim = currentPlayer.level().dimension();
                    BlockPos currentPos = currentPlayer.blockPosition();

                    // 添加几个模拟玩家
                    mockPlayers.add(new PlayerInfo(
                            UUID.fromString("11111111-1111-1111-1111-111111111111"),
                            "测试玩家_Alex",
                            currentDim,
                            new BlockPos(currentPos.getX() + 10, currentPos.getY(), currentPos.getZ() + 10)
                    ));

                    mockPlayers.add(new PlayerInfo(
                            UUID.fromString("22222222-2222-2222-2222-222222222222"),
                            "测试玩家_Steve",
                            currentDim,
                            new BlockPos(currentPos.getX() - 10, currentPos.getY(), currentPos.getZ() - 10)
                    ));

                    mockPlayers.add(new PlayerInfo(
                            UUID.fromString("33333333-3333-3333-3333-333333333333"),
                            "测试玩家_Neo",
                            currentDim,
                            new BlockPos(currentPos.getX(), currentPos.getY() + 5, currentPos.getZ() + 20)
                    ));

                    return mockPlayers;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, ModMenus.PlayerSelectionMenuData> getScreenStreamCodec() {
                    return ModMenus.PlayerSelectionMenuData.STREAM_CODEC;
                }
            });
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * 获取服务器在线玩家列表，排除当前玩家
     */
    private List<ServerPlayer> getPlayerList(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server != null) {
            return server.getPlayerList().getPlayers().stream()
                    .filter(otherPlayer -> !otherPlayer.getUUID().equals(player.getUUID()))
                    .toList();
        }
        return List.of();
    }

    /**
     * 获取模拟玩家信息列表（仅用于测试UI显示）
     */
    private List<PlayerInfo> getMockPlayerInfos(ServerPlayer currentPlayer) {
        List<PlayerInfo> mockPlayers = new ArrayList<>();
        ResourceKey<Level> currentDim = currentPlayer.level().dimension();
        BlockPos currentPos = currentPlayer.blockPosition();

        // 添加几个模拟玩家
        mockPlayers.add(new PlayerInfo(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "测试玩家_Alex",
                currentDim,
                new BlockPos(currentPos.getX() + 10, currentPos.getY(), currentPos.getZ() + 10)
        ));

        mockPlayers.add(new PlayerInfo(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "测试玩家_Steve",
                currentDim,
                new BlockPos(currentPos.getX() - 10, currentPos.getY(), currentPos.getZ() - 10)
        ));

        mockPlayers.add(new PlayerInfo(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "测试玩家_Neo",
                currentDim,
                new BlockPos(currentPos.getX(), currentPos.getY() + 5, currentPos.getZ() + 20)
        ));

        return mockPlayers;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
