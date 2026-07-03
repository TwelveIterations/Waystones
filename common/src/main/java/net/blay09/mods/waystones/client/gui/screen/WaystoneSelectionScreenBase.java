package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.client.gui.widget.AbstractWaystoneList;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystonesButton;
import net.blay09.mods.waystones.client.gui.widget.SortWaystonesButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneGroupFilterButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneList;
import net.blay09.mods.waystones.comparator.DistanceToPlayerComparator;
import net.blay09.mods.waystones.comparator.PreferSameDimensionComparator;
import net.blay09.mods.waystones.comparator.UserSortingComparator;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public abstract class WaystoneSelectionScreenBase extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private static final ResourceLocation EDIT_ICON = id("widgets/edit");

    protected final Collection<UserDecoratedWaystone> waystones;
    private final Inventory playerInventory;
    protected List<Waystone> filteredWaystones;

    private String searchText = "";

    private @Nullable AbstractWaystoneList<?> waystoneList;
    private @Nullable EditBox searchBox;
    private SortWaystonesButton.Mode sortMode = SortWaystonesButton.Mode.MANUAL;
    private @Nullable ResourceLocation activeGroupFilter;
    private int headerY;
    private boolean isLocationHeaderHovered;

    private static final int HEADER_WIDTH = AbstractWaystoneList.ENTRY_WIDTH;
    protected static final int HEADER_HEIGHT = 64;
    protected static final int FOOTER_HEIGHT = 25;
    private static final int SORT_BUTTON_WIDTH = 20;
    private static final int MARGIN = 2;

    public WaystoneSelectionScreenBase(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageWidth = 270;
        imageHeight = 200;
        this.playerInventory = playerInventory;
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
        super.init();

        waystoneList = createWaystoneList();
        addRenderableWidget(waystoneList);

        updateList();

        final int searchBoxWidth = allowSorting()
                ? HEADER_WIDTH - MARGIN - SORT_BUTTON_WIDTH
                : HEADER_WIDTH;
        searchBox = new EditBox(font,
                width / 2 - HEADER_WIDTH / 2,
                topPos + HEADER_HEIGHT - 24,
                searchBoxWidth,
                20,
                Component.empty());
        searchBox.setResponder(text -> {
            searchText = text;
            waystoneList.setScrollAmount(0);
            updateList();
        });

        addRenderableWidget(searchBox);
        if (allowSorting()) {
            final var sortButton = new SortWaystonesButton(
                    searchBox.getX() + searchBox.getWidth() + MARGIN,
                    searchBox.getY(),
                    sortMode,
                    mode -> {
                        sortMode = mode;
                        waystoneList.setScrollAmount(0);
                        updateList();
                    });
            addRenderableWidget(sortButton);
        }
        initSideButtons();
    }

    protected AbstractWaystoneList<?> createWaystoneList() {
        return new WaystoneList(leftPos + (imageWidth - AbstractWaystoneList.ENTRY_WIDTH) / 2,
                topPos + HEADER_HEIGHT,
                AbstractWaystoneList.ENTRY_WIDTH,
                imageHeight - HEADER_HEIGHT - FOOTER_HEIGHT,
                menu);
    }

    protected void initSideButtons() {
        int y = searchBox != null ? searchBox.getY() : topPos;
        if (allowReordering() || allowDeletion()) {
            final var manageButton = new ManageWaystonesButton(
                    leftPos - 8,
                    y,
                    button -> openManageScreen());
            addRenderableWidget(manageButton);
            y += manageButton.getHeight() + MARGIN;
        }

        y += 5;

        for (final var group : getShownGroupFilters()) {
            final var groupId = group.identifier();
            final var groupButton = new WaystoneGroupFilterButton(leftPos - 8,
                    y,
                    group,
                    () -> groupId.equals(activeGroupFilter),
                    button -> {
                        activeGroupFilter = groupId.equals(activeGroupFilter) ? null : groupId;
                        if (waystoneList != null) {
                            waystoneList.setScrollAmount(0);
                        }
                        updateList();
                    });
            addRenderableWidget(groupButton);
            y += groupButton.getHeight() + MARGIN;
        }
    }

    private void openManageScreen() {
        Minecraft.getInstance().setScreen(new ManageWaystonesScreen(menu, playerInventory, this));
    }

    protected void updateList() {
        List<Waystone> list = new ArrayList<>();
        for (Waystone waystone : waystones) {
            if (!shouldShowWaystone(waystone)) {
                continue;
            }

            if (!ignoresFilters(waystone) && activeGroupFilter != null && !waystone.getWaystoneGroups().contains(activeGroupFilter)) {
                continue;
            }

            if (ignoresFilters(waystone) || waystone.getEffectiveName().getString().toLowerCase().contains(searchText.toLowerCase())) {
                list.add(waystone);
            }
        }
        final var sorting = getSorting();
        if (sorting != null) {
            list.sort(sorting);
        }
        filteredWaystones = list;

        headerY = 0;

        if (waystoneList != null) {
            waystoneList.setWaystones(filteredWaystones);
        }
    }

    protected boolean shouldShowWaystone(Waystone waystone) {
        return true;
    }

    protected boolean ignoresFilters(Waystone waystone) {
        return isReturnPortal(waystone);
    }

    protected static boolean isReturnPortal(Waystone waystone) {
        final var waystoneType = waystone.getWaystoneType();
        return WaystoneTypes.WARP_PORTAL.equals(waystoneType) || WaystoneTypes.FLEETING_MEMORIAL.equals(waystoneType);
    }

    private List<WaystoneGroup> getShownGroupFilters() {
        final var nonEmptyGroups = new HashSet<ResourceLocation>();
        for (final var waystone : waystones) {
            nonEmptyGroups.addAll(waystone.getWaystoneGroups());
        }

        final var shownGroups = new ArrayList<WaystoneGroup>();
        final var groupRegistry = PlayerWaystoneManager.getWaystoneGroupRegistry(playerInventory.player);
        for (final var group : groupRegistry) {
            if (!group.hidden() && nonEmptyGroups.contains(group.identifier())) {
                shownGroups.add(group);
                if (shownGroups.size() >= 5) {
                    break;
                }
            }
        }

        return shownGroups;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isLocationHeaderHovered && menu.getWaystoneFrom() != null) {
            Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(menu.getWaystoneFrom().getPos()));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (waystoneList != null && waystoneList.isMouseOver(x, y)) {
            return waystoneList.mouseScrolled(x, y, scrollX, scrollY);
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Waystone fromWaystone = menu.getWaystoneFrom();
        final int locationHeaderY = headerY + 20;
        guiGraphics.drawCenteredString(font, getTitle(), imageWidth / 2, fromWaystone != null ? headerY : locationHeaderY, 0xFFFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(guiGraphics, fromWaystone, mouseX, mouseY, imageWidth / 2, locationHeaderY);
        }

        if (waystones.isEmpty()) {
            guiGraphics.drawCenteredString(font,
                    ChatFormatting.RED + I18n.get(getNoWaystonesMessageKey()),
                    imageWidth / 2,
                    imageHeight / 2 - 20,
                    0xFFFFFFFF);
        }
    }

    protected String getNoWaystonesMessageKey() {
        final var targetKind = menu.getTargetKind();
        if (targetKind != null && WaystoneTypes.isSharestone(targetKind)) {
            return "gui.waystones.waystone_selection.no_sharestones_available";
        }

        if (this instanceof SharestoneSelectionScreen) {
            return "gui.waystones.waystone_selection.no_sharestones_available";
        }

        return "gui.waystones.waystone_selection.no_waystones_activated";
    }

    private void drawLocationHeader(GuiGraphics guiGraphics, Waystone waystone, int mouseX, int mouseY, int x, int y) {
        Font font = Minecraft.getInstance().font;

        int locationPrefixWidth = font.width(Component.translatable("gui.waystones.waystone_selection.current_location", ""));

        var effectiveName = waystone.getEffectiveName().copy();
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
        guiGraphics.drawString(font, fullText, x - fullWidth / 2, y, 0xFFFFFFFF);

        if (isLocationHeaderHovered) {
            guiGraphics.blitSprite(EDIT_ICON, x + fullWidth / 2, y - 4, 16, 16);
        }
    }

    protected boolean allowReordering() {
        return true;
    }

    protected boolean allowDeletion() {
        return true;
    }

    protected boolean allowSorting() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (!this.searchBox.isFocused() || (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc())) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    }

    public @Nullable Comparator<Waystone> getSorting() {
        final var player = Minecraft.getInstance().player;
        final var manualSorting = getManualSorting();
        final Comparator<Waystone> sorting = switch (sortMode) {
            case MANUAL -> manualSorting;
            case NAME -> {
                final Comparator<Waystone> nameSorting = Comparator.comparing(
                        waystone -> waystone.getEffectiveName().getString(),
                        String.CASE_INSENSITIVE_ORDER);
                yield manualSorting != null ? nameSorting.thenComparing(manualSorting) : nameSorting;
            }
            case DISTANCE -> {
                final Comparator<Waystone> distanceSorting = new PreferSameDimensionComparator(player.level().dimension())
                        .thenComparing(new DistanceToPlayerComparator(player));
                yield manualSorting != null ? distanceSorting.thenComparing(manualSorting) : distanceSorting;
            }
        };
        return pinReturnPortalFirst(sorting);
    }

    protected @Nullable Comparator<Waystone> getManualSorting() {
        final var sortingIndex = PlayerWaystoneManager.getSortingIndex(playerInventory.player);
        return new UserSortingComparator(sortingIndex);
    }

    private Comparator<Waystone> pinReturnPortalFirst(@Nullable Comparator<Waystone> sorting) {
        final Comparator<Waystone> returnPortalSorting = Comparator.comparing(WaystoneSelectionScreenBase::isReturnPortal).reversed();
        return sorting != null ? returnPortalSorting.thenComparing(sorting) : returnPortalSorting;
    }

}
