package net.blay09.mods.waystones.util;

public class ExperienceUtils {
    public static int calculateLevelCostFromExperiencePoints(int currentLevel, int xpLoss) {
        return currentLevel - calculateLevelMinusExperiencePoints(currentLevel, xpLoss);
    }

    public static int calculateMinimumLevelForExperiencePoints(int requiredXp) {
        if (requiredXp <= 0) {
            return 0;
        }

        int cumulativeXp = 0;
        int level = 0;
        while (cumulativeXp < requiredXp) {
            cumulativeXp += getXpNeededForNextLevel(level);
            level++;
        }

        return level;
    }

    public static int calculateDisplayedLevelCostFromExperiencePoints(int currentLevel, int availableXp, int requiredXp) {
        if (requiredXp <= 0) {
            return 0;
        }

        if (availableXp >= requiredXp) {
            return Math.max(1, calculateLevelCostFromExperiencePoints(currentLevel, requiredXp));
        }

        return calculateMinimumLevelForExperiencePoints(requiredXp);
    }

    private static int calculateLevelMinusExperiencePoints(int currentLevel, int xpLoss) {
        int currentCumulativeXp = getCumulativeXpNeededForLevel(currentLevel);

        int remainingXp = currentCumulativeXp - xpLoss;

        int newLevel = 0;
        int newCumulativeXp = 0;
        for (int level = 0; level <= currentLevel; level++) {
            newCumulativeXp += getXpNeededForNextLevel(level);
            if (remainingXp < newCumulativeXp) {
                newLevel = level;
                break;
            }
        }

        return newLevel;
    }

    private static int getXpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static int getCumulativeXpNeededForLevel(int targetLevel) {
        int currentCumulativeXp = 0;
        for (int level = 0; level < targetLevel; level++) {
            currentCumulativeXp += getXpNeededForNextLevel(level);
        }
        return currentCumulativeXp;
    }
}
