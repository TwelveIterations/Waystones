package net.blay09.mods.waystones.worldgen.namegen;

import net.minecraft.util.RandomSource;

public class MrPorkNameGenerator implements INameGenerator {

    private static final String[] random1 = new String[]{
            "Kr", "Ca", "Ra",
            "Rei", "Mar", "Luk", "Cro", "Cru", "Ray", "Bre", "Zed", "Mor", "Jag", "Mer", "Jar", "Mad", "Cry", "Zur",
            "Mjol", "Zork", "Creo", "Azak", "Azur", "Mrok", "Drak",
    };

    private static final String[] random2 = new String[]{
            "ir", "mi",
            "air", "sor", "mee", "clo", "red", "cra", "ark", "arc", "mur", "zer",
            "miri", "lori", "cres", "zoir", "urak",
            "marac",
            "slamar", "salmar",
    };

    private static final String[] random3 = new String[]{
            "d",
            "ed", "es", "er",
            "ark", "arc", "der", "med", "ure", "zur", "mur",
            "tron", "cred",
    };

    @Override
    public String randomName(RandomSource rand) {
        return random1[rand.nextInt(random1.length)] + random2[rand.nextInt(random2.length)] + random3[rand.nextInt(random3.length)];
    }

}
