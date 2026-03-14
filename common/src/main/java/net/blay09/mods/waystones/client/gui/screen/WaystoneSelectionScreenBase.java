package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.blay09.mods.waystones.client.gui.widget.RemoveWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.SortWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneButton;
import net.blay09.mods.waystones.comparator.UserSortingComparator;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.ServerboundRemoveWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestEditWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundSelectWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundSortWaystonePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
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

public abstract class WaystoneSelectionScreenBase extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private final Collection<Waystone> waystones;
    private List<Waystone> filteredWaystones;
    private final List<ITooltipProvider> tooltipProviders = new ArrayList<>();

    private String searchText = "";

    private Button btnPrevPage;
    private Button btnNextPage;
    private EditBox searchBox;
    private int pageOffset;
    private int headerY;
    private boolean isLocationHeaderHovered;
    private int buttonsPerPage;

    private static final int headerHeight = 64;
    private static final int footerHeight = 25;
    private static final int entryHeight = 25;

    public WaystoneSelectionScreenBase(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 270, 200);
        waystones = container.getWaystones();
        PlayerWaystoneManager.ensureSortingIndex(Minecraft.getInstance().player, waystones);
        filteredWaystones = new ArrayList<>(waystones);
        final var sorting = getSorting();
        if (sorting != null) {
            filteredWaystones.sort(getSorting());
        }
    }

    @Override
    public void init() {
        final int maxContentHeight = (int) (height * 0.6f);
        final int maxButtonsPerPage = (maxContentHeight - headerHeight - footerHeight) / entryHeight;
        buttonsPerPage = Math.max(4, Math.min(maxButtonsPerPage, waystones.size()));

        super.init();

        tooltipProviders.clear();
        btnPrevPage = Button.builder(Component.translatable("gui.waystones.waystone_selection.previous_page"), _ -> {
            pageOffset = Kuma.hasShiftDown() ? 0 : pageOffset - 1;
            updateList();
        }).pos(width / 2 - 100, height / 2 + 40).size(95, 20).build();
        addRenderableWidget(btnPrevPage);

        btnNextPage = Button.builder(Component.translatable("gui.waystones.waystone_selection.next_page"), _ -> {
            pageOffset = Kuma.hasShiftDown() ? (waystones.size() - 1) / buttonsPerPage : pageOffset + 1;
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
        List<Waystone> list = new ArrayList<>();
        for (Waystone waystone : waystones) {
            if (waystone.getName().getString().toLowerCase().contains(searchText.toLowerCase())) {
                list.add(waystone);
            }
        }
        final var sorting = getSorting();
        if (sorting != null) {
            list.sort(sorting);
        }
        filteredWaystones = list;

        headerY = 0;

        btnPrevPage.active = pageOffset > 0;
        btnNextPage.active = pageOffset < (filteredWaystones.size() - 1) / buttonsPerPage;

        tooltipProviders.clear();

        Predicate<Object> removePredicate = button -> button instanceof WaystoneButton || button instanceof SortWaystoneButton || button instanceof RemoveWaystoneButton;
        ((ScreenAccessor) this).balm$getChildren().removeIf(removePredicate);
        ((ScreenAccessor) this).balm$getNarratables().removeIf(removePredicate);
        ((ScreenAccessor) this).balm$getRenderables().removeIf(removePredicate);

        int y = topPos + headerHeight + headerY;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < filteredWaystones.size()) {
                Waystone waystone = filteredWaystones.get(entryIndex);

                addRenderableWidget(createWaystoneButton(y, waystone, menu.getWarpRequirements(waystone)));

                if (allowSorting()) {
                    SortWaystoneButton sortUpButton = new SortWaystoneButton(width / 2 + 108, y + 2, -1, y, 20, _ -> sortWaystone(waystone, -1));
                    if (entryIndex == 0) {
                        sortUpButton.active = false;
                    }
                    addRenderableWidget(sortUpButton);

                    SortWaystoneButton sortDownButton = new SortWaystoneButton(width / 2 + 108, y + 13, 1, y, 20, _ -> sortWaystone(waystone, 1));
                    if (entryIndex == filteredWaystones.size() - 1) {
                        sortDownButton.active = false;
                    }
                    addRenderableWidget(sortDownButton);
                }

                if (allowDeletion(waystone)) {
                    RemoveWaystoneButton removeButton = new RemoveWaystoneButton(width / 2 + 122, y + 4, y, 20, waystone, _ -> {
                        Player player = Minecraft.getInstance().player;
                        PlayerWaystoneManager.deactivateWaystone(Objects.requireNonNull(player), waystone);
                        waystones.remove(waystone);
                        Balm.networking().sendToServer(new ServerboundRemoveWaystonePacket(waystone.getWaystoneUid()));
                        updateList();
                    });
                    addRenderableWidget(removeButton);
                }

                y += 22;
            }
        }

        btnPrevPage.setY(topPos + headerY + headerHeight + buttonsPerPage * 22 + (!filteredWaystones.isEmpty() ? 10 : 0));
        btnNextPage.setY(topPos + headerY + headerHeight + buttonsPerPage * 22 + (!filteredWaystones.isEmpty() ? 10 : 0));
    }

    private boolean allowDeletion(Waystone waystone) {
        final var isCreative = Minecraft.getInstance().player.getAbilities().instabuild;
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && !isCreative) {
            return false;
        }

        if (WaystoneKinds.isSharestone(waystone.getWaystoneKind())) {
            if (!isCreative) {
                return false;
            }
        } else if (!waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)) {
            return false;
        }

        return allowDeletion();
    }

    private WaystoneButton createWaystoneButton(int y, final Waystone waystone, Either<List<Object>, List<Object>> requirements) {
        final var waystoneFrom = menu.getWaystoneFrom();
        WaystoneButton btnWaystone = new WaystoneButton(width / 2 - 100, y, waystone, requirements, _ -> onWaystoneSelected(waystone));
        if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
            btnWaystone.active = false;
        }
        return btnWaystone;
    }

    protected void onWaystoneSelected(Waystone waystone) {
        Balm.networking().sendToServer(new ServerboundSelectWaystonePacket(waystone.getWaystoneUid()));
    }

    private void sortWaystone(Waystone waystone, int sortDir) {
        final var waystoneUid = waystone.getWaystoneUid();
        if (Kuma.hasShiftDown()) {
            if (sortDir == -1) {
                PlayerWaystoneManager.sortWaystoneAsFirst(Minecraft.getInstance().player, waystoneUid);
                Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, ServerboundSortWaystonePacket.SORT_FIRST));
            } else if (sortDir == 1) {
                PlayerWaystoneManager.sortWaystoneAsLast(Minecraft.getInstance().player, waystoneUid);
                Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, ServerboundSortWaystonePacket.SORT_LAST));
            }
        } else {
            final var index = filteredWaystones.indexOf(waystone);
            final var otherIndex = index + sortDir;
            if (index == -1 || otherIndex < 0 || otherIndex >= waystones.size()) {
                return;
            }
            final var otherWaystone = filteredWaystones.get(otherIndex);
            final var otherWaystoneUid = otherWaystone.getWaystoneUid();

            PlayerWaystoneManager.sortWaystoneSwap(Minecraft.getInstance().player, waystoneUid, otherWaystoneUid);
            Balm.networking().sendToServer(new ServerboundSortWaystonePacket(waystoneUid, otherWaystoneUid));
        }

        updateList();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isLocationHeaderHovered && menu.getWaystoneFrom() != null) {
            Balm.networking().sendToServer(new ServerboundRequestEditWaystonePacket(menu.getWaystoneFrom().getPos()));
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        extractTooltip(guiGraphics, mouseX, mouseY);
        for (ITooltipProvider tooltipProvider : tooltipProviders) {
            if (tooltipProvider.shouldShowTooltip()) {
                guiGraphics.setTooltipForNextFrame(font, tooltipProvider.getTooltipComponents(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Waystone fromWaystone = menu.getWaystoneFrom();
        guiGraphics.centeredText(font, getTitle(), imageWidth / 2, headerY + (fromWaystone != null ? 20 : 0), 0xFFFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(guiGraphics, fromWaystone, mouseX, mouseY, imageWidth / 2, headerY);
        }

        if (waystones.isEmpty()) {
            guiGraphics.centeredText(font,
                    ChatFormatting.RED + I18n.get("gui.waystones.waystone_selection.no_waystones_activated"),
                    imageWidth / 2,
                    imageHeight / 2 - 20,
                    0xFFFFFFFF);
        }
    }

    private void drawLocationHeader(GuiGraphicsExtractor guiGraphics, Waystone waystone, int mouseX, int mouseY, int x, int y) {
        Font font = Minecraft.getInstance().font;

        int locationPrefixWidth = font.width(Component.translatable("gui.waystones.waystone_selection.current_location", ""));

        var effectiveName = waystone.getName().copy();
        if (effectiveName.getString().isEmpty()) {
            effectiveName = Component.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        }
        int locationWidth = font.width(effectiveName);

        int fullWidth = locationPrefixWidth + locationWidth;

        int startX = leftPos + x - fullWidth / 2 + locationPrefixWidth;
        int startY = y + topPos;
        isLocationHeaderHovered = mouseX >= startX && mouseX < startX + locationWidth + 16
                && mouseY >= startY && mouseY < startY + font.lineHeight;

        if (isLocationHeaderHovered) {
            effectiveName.withStyle(ChatFormatting.UNDERLINE);
        }

        final var fullText = Component.translatable("gui.waystones.waystone_selection.current_location",
                effectiveName.withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.YELLOW);
        guiGraphics.text(font, fullText, x - fullWidth / 2, y, 0xFFFFFFFF);

        if (isLocationHeaderHovered) {
            var poseStack = guiGraphics.pose();
            poseStack.pushMatrix();
            float scale = 0.5f;
            poseStack.translate(x + fullWidth / 2f + 4, y);
            poseStack.scale(scale, scale);
            guiGraphics.item(new ItemStack(Items.WRITABLE_BOOK), 0, 0);
            poseStack.popMatrix();
        }
    }

    protected boolean allowSorting() {
        return true;
    }

    protected boolean allowDeletion() {
        return true;
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

    public Comparator<Waystone> getSorting() {
        final var player = Minecraft.getInstance().player;
        final var sortingIndex = PlayerWaystoneManager.getSortingIndex(player);
        return new UserSortingComparator(sortingIndex);
    }
}
