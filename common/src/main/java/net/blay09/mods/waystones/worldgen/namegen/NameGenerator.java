package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class NameGenerator extends SavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String USED_NAMES = "UsedNames";
    private static final NameGenerator clientStorageCopy = new NameGenerator();

    private final Set<String> usedNames = Sets.newHashSet();

    private INameGenerator getNameGenerator(NameGenerationMode nameGenerationMode) {
        switch (nameGenerationMode) {
            case MIXED:
                return new MixedNameGenerator(new MrPorkNameGenerator(), new CustomNameGenerator(false, usedNames));
            case RANDOM_ONLY:
                return new MrPorkNameGenerator();
            case PRESET_ONLY:
                return new CustomNameGenerator(true, usedNames);
            case PRESET_FIRST:
            default:
                return new SequencedNameGenerator(new CustomNameGenerator(false, usedNames), new MrPorkNameGenerator());
        }
    }

    public synchronized String getName(IWaystone waystone, RandomSource rand, NameGenerationMode nameGenerationMode) {
        INameGenerator nameGenerator = getNameGenerator(nameGenerationMode);
        String originalName = nameGenerator.randomName(rand);
        if (originalName == null) {
            // This should never happen, but just in case generate a fallback if something did go wrong
            originalName = Objects.requireNonNull(new MrPorkNameGenerator().randomName(rand));
        }
        String name = resolveDuplicate(originalName);

        GenerateWaystoneNameEvent event = new GenerateWaystoneNameEvent(waystone, name);
        Balm.getEvents().fireEvent(event);
        name = event.getName();

        usedNames.add(name);
        setDirty();
        return name;
    }

    private String resolveDuplicate(String name) {
        String tryName = name;
        int i = 1;
        while (usedNames.contains(tryName)) {
            tryName = name + " " + RomanNumber.toRoman(i);
            i++;
        }
        return tryName;
    }

    public static NameGenerator load(CompoundTag compound) {
        NameGenerator nameGenerator = new NameGenerator();
        ListTag tagList = compound.getList(USED_NAMES, Tag.TAG_STRING);
        for (Tag tag : tagList) {
            nameGenerator.usedNames.add(tag.getAsString());
        }
        return nameGenerator;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag tagList = new ListTag();
        for (String entry : usedNames) {
            tagList.add(StringTag.valueOf(entry));
        }
        compound.put(USED_NAMES, tagList);
        return compound;
    }

    public static NameGenerator get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(NameGenerator::load, NameGenerator::new, DATA_NAME);
        }

        return clientStorageCopy;
    }

}
