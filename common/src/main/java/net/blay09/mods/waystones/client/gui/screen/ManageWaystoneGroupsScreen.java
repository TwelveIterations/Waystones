package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.gui.widget.AbstractWaystoneList;
import net.blay09.mods.waystones.client.gui.widget.BackToWaystoneSelectionButton;
import net.blay09.mods.waystones.client.gui.widget.CreateWaystoneGroupButton;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystoneGroupsList;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public class ManageWaystoneGroupsScreen extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private static final ResourceLocation EDIT_ICON = id("widgets/edit");

    private static final int HEADER_WIDTH = AbstractWaystoneList.ENTRY_WIDTH;
    private static final int HEADER_HEIGHT = 64;
    private static final int FOOTER_HEIGHT = 25;
    private static final int CREATE_BUTTON_WIDTH = 20;
    private static final int MARGIN = 2;

    private final ManageWaystonesScreen parent;
    private final Inventory playerInventory;
    private final List<WaystoneGroup> groups;
    private List<WaystoneGroup> filteredGroups;
    private String searchText = "";

    private @Nullable ManageWaystoneGroupsList groupList;
    private @Nullable EditBox searchBox;
    private boolean isLocationHeaderHovered;

    public ManageWaystoneGroupsScreen(WaystoneSelectionMenu menu, Inventory playerInventory, ManageWaystonesScreen parent) {
        super(menu, playerInventory, Component.translatable("container.waystones.manage_groups"));
        imageWidth = 270;
        imageHeight = 200;
        this.parent = parent;
        this.playerInventory = playerInventory;
        this.groups = new ArrayList<>(PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT)
                .getWaystoneGroupRegistry(playerInventory.player)
                .stream()
                .sorted(Comparator.comparingInt(WaystoneGroup::sortIndex)
                        .thenComparing(group -> group.name().getString(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(group -> group.identifier().toString()))
                .toList());
        this.filteredGroups = new ArrayList<>(groups);
    }

    @Override
    public void init() {
        super.init();

        groupList = new ManageWaystoneGroupsList(leftPos,
                topPos + HEADER_HEIGHT,
                imageWidth,
                imageHeight - HEADER_HEIGHT - FOOTER_HEIGHT,
                this);
        addRenderableWidget(groupList);

        final int searchBoxWidth = HEADER_WIDTH - MARGIN - CREATE_BUTTON_WIDTH;
        searchBox = new EditBox(font,
                width / 2 - HEADER_WIDTH / 2,
                topPos + HEADER_HEIGHT - 24,
                searchBoxWidth,
                20,
                Component.empty());
        searchBox.setResponder(text -> {
            searchText = text;
            groupList.setScrollAmount(0);
            updateList();
        });
        addRenderableWidget(searchBox);

        final var createButton = new CreateWaystoneGroupButton(
                searchBox.getX() + searchBox.getWidth() + MARGIN,
                searchBox.getY(),
                button -> createGroup());
        addRenderableWidget(createButton);

        final var backButton = new BackToWaystoneSelectionButton(leftPos - 8,
                topPos + HEADER_HEIGHT - 24,
                button -> returnToManageWaystones());
        addRenderableWidget(backButton);

        updateList();
    }

    private void updateList() {
        final var normalizedSearchText = searchText.toLowerCase(Locale.ROOT);
        filteredGroups = groups.stream()
                .filter(group -> group.name().getString().toLowerCase(Locale.ROOT).contains(normalizedSearchText)
                        || group.identifier().toString().toLowerCase(Locale.ROOT).contains(normalizedSearchText))
                .toList();

        if (groupList != null) {
            groupList.setGroups(filteredGroups);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (groupList != null && groupList.isMouseOver(x, y)) {
            return groupList.mouseScrolled(x, y, scrollX, scrollY);
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (isLocationHeaderHovered && menu.getWaystoneFrom() != null) {
            Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(menu.getWaystoneFrom().getPos()));
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Waystone fromWaystone = menu.getWaystoneFrom();
        final int locationHeaderY = 20;
        guiGraphics.drawCenteredString(font, getTitle(), imageWidth / 2, fromWaystone != null ? 0 : locationHeaderY, 0xFFFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(guiGraphics, fromWaystone, mouseX, mouseY, imageWidth / 2, locationHeaderY);
        }

        if (groups.isEmpty()) {
            guiGraphics.drawCenteredString(font,
                    ChatFormatting.RED + I18n.get("gui.waystones.manage_groups.no_groups"),
                    imageWidth / 2,
                    imageHeight / 2 - 20,
                    0xFFFFFFFF);
        }
    }

    private void drawLocationHeader(GuiGraphics guiGraphics, Waystone waystone, int mouseX, int mouseY, int x, int y) {
        final var font = Minecraft.getInstance().font;

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

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (!this.searchBox.isFocused() || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        returnToManageWaystones();
    }

    private void returnToManageWaystones() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void createGroup() {
        final var initialName = searchText;
        final var groupId = id("user/" + UUID.randomUUID());
        Minecraft.getInstance().setScreen(new WaystoneGroupEditScreen(menu, playerInventory, this, groupId, initialName));
    }

    public void editGroup(WaystoneGroup group) {
        Minecraft.getInstance().setScreen(new WaystoneGroupEditScreen(menu, playerInventory, this, group.identifier(), group.name().getString()));
    }

    public void deleteGroup(WaystoneGroup group) {
        if (group.inbuilt()) {
            return;
        }

        groups.removeIf(it -> it.identifier().equals(group.identifier()));
        updateList();
        Balm.getNetworking().sendToServer(new ServerboundRemoveWaystoneGroupPacket(group.identifier()));
    }

    public void reorderGroup(WaystoneGroup group, WaystoneGroup otherGroup) {
        final int index = indexOfGroup(groups, group);
        final int otherIndex = indexOfGroup(groups, otherGroup);
        if (index == -1 || otherIndex == -1) {
            return;
        }

        Collections.swap(groups, index, otherIndex);
        syncGroupOrder();
        Balm.getNetworking().sendToServer(new ServerboundSortWaystoneGroupPacket(group.identifier(), otherGroup.identifier()));
    }

    public void moveGroupToTop(WaystoneGroup group) {
        final int index = indexOfGroup(groups, group);
        if (index == -1) {
            return;
        }

        groups.remove(index);
        groups.addFirst(group);
        syncGroupOrder();
        Balm.getNetworking().sendToServer(new ServerboundSortWaystoneGroupPacket(group.identifier(), ServerboundSortWaystoneGroupPacket.SORT_FIRST));
    }

    public void moveGroupToBottom(WaystoneGroup group) {
        final int index = indexOfGroup(groups, group);
        if (index == -1) {
            return;
        }

        groups.remove(index);
        groups.add(group);
        syncGroupOrder();
        Balm.getNetworking().sendToServer(new ServerboundSortWaystoneGroupPacket(group.identifier(), ServerboundSortWaystoneGroupPacket.SORT_LAST));
    }

    public void toggleGroupHidden(WaystoneGroup group) {
        if (!group.inbuilt()) {
            return;
        }

        final var updatedGroup = new WaystoneGroupImpl(group.identifier(), group.name(), group.icon(), group.color(), group.inbuilt(), !group.hidden(), group.sortIndex());
        groups.replaceAll(it -> it.identifier().equals(group.identifier()) ? updatedGroup : it);
        updateList();

        final var store = PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        final var registry = new ArrayList<>(store.getWaystoneGroupRegistry(playerInventory.player));
        registry.replaceAll(it -> it.identifier().equals(group.identifier()) ? updatedGroup : it);
        store.setWaystoneGroupRegistry(playerInventory.player, registry);
        Balm.getNetworking().sendToServer(new ServerboundEditWaystoneGroupPacket(group.identifier(), group.name().getString(), updatedGroup.icon(), updatedGroup.color(), updatedGroup.hidden()));
    }

    private void syncGroupOrder() {
        final var store = PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        store.setWaystoneGroupRegistry(playerInventory.player, groups);
        groups.clear();
        groups.addAll(store.getWaystoneGroupRegistry(playerInventory.player));
        filteredGroups = filteredGroups.stream()
                .map(group -> groups.stream()
                        .filter(it -> it.identifier().equals(group.identifier()))
                        .findFirst()
                        .orElse(group))
                .toList();
    }

    private static int indexOfGroup(List<WaystoneGroup> groups, WaystoneGroup group) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).identifier().equals(group.identifier())) {
                return i;
            }
        }
        return -1;
    }

    void returnFromEdit() {
        Minecraft.getInstance().setScreen(new ManageWaystoneGroupsScreen(menu, playerInventory, parent));
    }
}
