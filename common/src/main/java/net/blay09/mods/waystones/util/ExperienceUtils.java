package net.blay09.mods.waystones.util;

public class ExperienceUtils {
    public static int calculateMinimumLevelForExperiencePoints(int requiredXp) {
        if (requiredXp <= 0) {
            return 0;
        }

        long cumulativeXp = 0;
        int level = 0;
        while (cumulativeXp < requiredXp) {
            cumulativeXp += getXpNeededForNextLevel(level);
            level++;
        }

        return level;
    }

    public static int calculateDisplayedLevelCostFromExperiencePoints(int currentLevel, long availableXp, int requiredXp) {
        if (requiredXp <= 0) {
            return 0;
        }

        if (availableXp >= requiredXp) {
            final var remainingXp = availableXp - requiredXp;
            final var newLevel = calculateLevelFromExperiencePoints(remainingXp);
            return currentLevel - newLevel;
        }

        return calculateMinimumLevelForExperiencePoints(requiredXp);
    }

    private static int calculateLevelFromExperiencePoints(long experiencePoints) {
        long cumulativeXp = 0;
        int level = 0;
        while (cumulativeXp + getXpNeededForNextLevel(level) <= experiencePoints) {
            cumulativeXp += getXpNeededForNextLevel(level);
            level++;
        }

        return level;
    }

    private static long getXpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9L;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5L : 7 + level * 2L;
        }
    }
}
