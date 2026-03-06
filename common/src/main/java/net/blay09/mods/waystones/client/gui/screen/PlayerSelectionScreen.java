package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.blay09.mods.waystones.api.PlayerInfo;
import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.PlayerSelectionMenu;
import net.blay09.mods.waystones.network.message.ServerboundTeleportToPlayerPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.function.Predicate;

/**
 * 玩家选择屏幕 - 显示可传送的玩家列表
 */
public class PlayerSelectionScreen extends AbstractContainerScreen<PlayerSelectionMenu> {

    private final List<PlayerInfo> playerInfos;
    private List<PlayerInfo> filteredPlayers;
    private final List<ITooltipProvider> tooltipProviders = new ArrayList<>();

    private String searchText = "";

    private Button btnPrevPage;
    private Button btnNextPage;
    private EditBox searchBox;
    private int pageOffset;
    private int headerY;
    private int buttonsPerPage;

    private static final int headerHeight = 64;
    private static final int footerHeight = 25;
    private static final int entryHeight = 25;

    public PlayerSelectionScreen(PlayerSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.playerInfos = container.getPlayerInfos();
        this.filteredPlayers = new ArrayList<>(playerInfos);
        imageWidth = 270;
        imageHeight = 200;
    }

    @Override
    public void init() {
        final int maxContentHeight = (int) (height * 0.6f);
        final int maxButtonsPerPage = (maxContentHeight - headerHeight - footerHeight) / entryHeight;
        buttonsPerPage = Math.max(4, Math.min(maxButtonsPerPage, playerInfos.size()));
        final int contentHeight = headerHeight + buttonsPerPage * entryHeight + footerHeight;

        // Leave no space for JEI!
        imageWidth = width;
        imageHeight = contentHeight;

        super.init();

        tooltipProviders.clear();
        btnPrevPage = Button.builder(Component.translatable("gui.waystones.waystone_selection.previous_page"), button -> {
            pageOffset = Math.max(0, pageOffset - 1);
            updateList();
        }).pos(width / 2 - 100, height / 2 + 40).size(95, 20).build();
        addRenderableWidget(btnPrevPage);

        btnNextPage = Button.builder(Component.translatable("gui.waystones.waystone_selection.next_page"), button -> {
            int maxPage = (filteredPlayers.size() - 1) / buttonsPerPage;
            pageOffset = Math.min(maxPage, pageOffset + 1);
            updateList();
        }).pos(width / 2 + 5, height / 2 + 40).size(95, 20).build();
        addRenderableWidget(btnNextPage);

        updateList();

        searchBox = new EditBox(font, width / 2 - 99, topPos + headerHeight - 24, 198, 20, Component.empty());
        searchBox.setResponder(text -> {
            pageOffset = 0;
            searchText = text;
            updateList();
        });

        addRenderableWidget(searchBox);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ITooltipProvider) {
            tooltipProviders.add((ITooltipProvider) widget);
        }
        return super.addRenderableWidget(widget);
    }

    private void updateList() {
        List<PlayerInfo> list = new ArrayList<>();
        for (PlayerInfo playerInfo : playerInfos) {
            if (playerInfo.name().toLowerCase().contains(searchText.toLowerCase())) {
                list.add(playerInfo);
            }
        }
        filteredPlayers = list;

        headerY = 0;

        btnPrevPage.active = pageOffset > 0;
        btnNextPage.active = pageOffset < (filteredPlayers.size() - 1) / buttonsPerPage;

        tooltipProviders.clear();

        // 移除旧的玩家按钮
        Predicate<Object> removePredicate = button -> button instanceof PlayerButton;
        ((ScreenAccessor) this).balm$getChildren().removeIf(removePredicate);
        ((ScreenAccessor) this).balm$getNarratables().removeIf(removePredicate);
        ((ScreenAccessor) this).balm$getRenderables().removeIf(removePredicate);

        int y = topPos + headerHeight + headerY;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < filteredPlayers.size()) {
                PlayerInfo targetPlayer = filteredPlayers.get(entryIndex);
                addRenderableWidget(createPlayerButton(y, targetPlayer));
                y += 22;
            }
        }

        btnPrevPage.setY(topPos + headerY + headerHeight + buttonsPerPage * 22 + (filteredPlayers.size() > 0 ? 10 : 0));
        btnNextPage.setY(topPos + headerY + headerHeight + buttonsPerPage * 22 + (filteredPlayers.size() > 0 ? 10 : 0));
    }

    private PlayerButton createPlayerButton(int y, final PlayerInfo playerInfo) {
        PlayerButton btnPlayer = new PlayerButton(width / 2 - 100, y, playerInfo, button -> onPlayerSelected(playerInfo));
        return btnPlayer;
    }

    protected void onPlayerSelected(PlayerInfo playerInfo) {
        Balm.networking().sendToServer(new ServerboundTeleportToPlayerPacket(playerInfo.uuid()));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
        for (ITooltipProvider tooltipProvider : tooltipProviders) {
            if (tooltipProvider.shouldShowTooltip()) {
                guiGraphics.setTooltipForNextFrame(font, tooltipProvider.getTooltipComponents(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(font, getTitle(), imageWidth / 2, headerY, 0xFFFFFFFF);

        if (playerInfos.size() == 0) {
            guiGraphics.drawCenteredString(font,
                    ChatFormatting.RED + I18n.get("gui.waystones.player_selection.no_players_online"),
                    imageWidth / 2,
                    imageHeight / 2 - 20,
                    0xFFFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.searchBox == null) {
            return super.keyPressed(event);
        }

        if (!this.searchBox.isFocused() || (event.isEscape() && this.shouldCloseOnEsc())) {
            return super.keyPressed(event);
        }

        return this.searchBox.keyPressed(event);
    }

    /**
     * 玩家按钮 - 用于显示玩家信息
     */
    private static class PlayerButton extends Button.Plain implements ITooltipProvider {
        private final PlayerInfo playerInfo;
        private final List<Component> tooltip;

        public PlayerButton(int x, int y, PlayerInfo playerInfo, OnPress onPress) {
            super(x, y, 200, 20, playerInfo.getDisplayName(), onPress, Button.DEFAULT_NARRATION);
            this.playerInfo = playerInfo;
            this.tooltip = new ArrayList<>();

            // 添加玩家名称
            tooltip.add(playerInfo.getDisplayName().copy().withStyle(ChatFormatting.WHITE));

            // 添加位置信息
            var pos = playerInfo.position();
            var dimension = playerInfo.dimension().identifier().toString();
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.current_location",
                    String.format("%s @ %s", pos, dimension)).withStyle(ChatFormatting.GRAY));

            // 添加成本信息（如果有）
            var config = WaystonesConfig.getActive().playerCall;
            if (config.xpCost > 0) {
                tooltip.add(Component.translatable("gui.waystones.player_selection.xp_cost", config.xpCost)
                        .withStyle(ChatFormatting.GREEN));
            }
            if (!config.costItem.isEmpty()) {
                tooltip.add(Component.translatable("gui.waystones.player_selection.item_cost",
                        config.costItem.getHoverName()).withStyle(ChatFormatting.GREEN));
            }
            if (config.cooldownSeconds > 0) {
                String cooldownText = formatCooldown(config.cooldownSeconds);
                tooltip.add(Component.translatable("gui.waystones.player_selection.cooldown", cooldownText)
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

        private String formatCooldown(int seconds) {
            if (seconds < 60) {
                return seconds + "s";
            }
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return minutes + "m " + remainingSeconds + "s";
        }

        @Override
        public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
            // 渲染玩家头像
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.renderItem(new ItemStack(Items.PLAYER_HEAD), getX() + 2, getY() + 2);
        }

        @Override
        public boolean shouldShowTooltip() {
            return isHovered();
        }

        @Override
        public List<Component> getTooltipComponents() {
            return tooltip;
        }
    }
}
