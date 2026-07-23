package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.network.chat.Component;

import java.util.List;

public record GroupColor(String serializedName, int rgb) {

    public static final GroupColor WHITE = new GroupColor("white", 0xFFFFFF);
    public static final GroupColor GOLD = new GroupColor("gold", 0xFFAA00);
    public static final GroupColor LIGHT_PURPLE = new GroupColor("light_purple", 0xFF55FF);
    public static final GroupColor AQUA = new GroupColor("aqua", 0x55FFFF);
    public static final GroupColor YELLOW = new GroupColor("yellow", 0xFFFF55);
    public static final GroupColor GREEN = new GroupColor("green", 0x55FF55);
    public static final GroupColor RED = new GroupColor("red", 0xFF5555);
    public static final GroupColor GRAY = new GroupColor("gray", 0xAAAAAA);
    public static final GroupColor DARK_GRAY = new GroupColor("dark_gray", 0x555555);
    public static final GroupColor DARK_AQUA = new GroupColor("dark_aqua", 0x00AAAA);
    public static final GroupColor DARK_PURPLE = new GroupColor("dark_purple", 0xAA00AA);
    public static final GroupColor BLUE = new GroupColor("blue", 0x5555FF);
    public static final GroupColor DARK_BLUE = new GroupColor("dark_blue", 0x0000AA);
    public static final GroupColor DARK_GREEN = new GroupColor("dark_green", 0x00AA00);
    public static final GroupColor DARK_RED = new GroupColor("dark_red", 0xAA0000);

    public static final List<GroupColor> COLORS = List.of(
            WHITE,
            GOLD,
            LIGHT_PURPLE,
            AQUA,
            YELLOW,
            GREEN,
            RED,
            GRAY,
            DARK_GRAY,
            DARK_AQUA,
            DARK_PURPLE,
            BLUE,
            DARK_BLUE,
            DARK_GREEN,
            DARK_RED);

    public int toArgb() {
        return rgb | 0xFF000000;
    }

    public Component getName() {
        return Component.translatable("color.waystones.group." + serializedName);
    }

    public static GroupColor fromArgb(int color) {
        final var rgb = color & 0x00FFFFFF;
        for (final var groupColor : COLORS) {
            if (groupColor.rgb() == rgb) {
                return groupColor;
            }
        }

        return getClosestColor(rgb);
    }

    public static int indexOf(GroupColor color) {
        final var index = COLORS.indexOf(color);
        return index != -1 ? index : 0;
    }

    private static GroupColor getClosestColor(int rgb) {
        var closestColor = WHITE;
        var closestDistance = Integer.MAX_VALUE;
        final var red = (rgb >> 16) & 0xFF;
        final var green = (rgb >> 8) & 0xFF;
        final var blue = rgb & 0xFF;

        for (final var groupColor : COLORS) {
            final var textRgb = groupColor.rgb();
            final var textRed = (textRgb >> 16) & 0xFF;
            final var textGreen = (textRgb >> 8) & 0xFF;
            final var textBlue = textRgb & 0xFF;
            final var redDistance = red - textRed;
            final var greenDistance = green - textGreen;
            final var blueDistance = blue - textBlue;
            final var distance = redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance;
            if (distance < closestDistance) {
                closestColor = groupColor;
                closestDistance = distance;
            }
        }

        return closestColor;
    }
}
